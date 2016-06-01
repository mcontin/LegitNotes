package com.legitdevs.legitnotes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.VideoView;
import android.widget.MediaController;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.legitdevs.legitnotes.filemanager.FileManager;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;
import com.roughike.bottombar.OnTabClickListener;


import java.io.File;
import java.text.DateFormat;
import java.util.Collections;

public class NoteDetailActivity extends AppCompatActivity {

    public final static String KEY_NOTE = "note";
    public final static String KEY_IMAGE = "image";
    private static final String TAG = "NoteDetailActivity";

    public static NoteDetailActivity activity;

    private Note note;
    private ScrollView scrollView;
    private TextView text, date;
    private ImageView imageNote;
    private BottomBar bottomBar;
    private boolean isImageFitToScreen = true;
    private RelativeLayout mediaContainer;
    private int audioIndex=100, imageIndex=100, videoIndex=100;
    private ViewGroup audioPlayer;
    private File audio, image, video;

    private VideoView myVideoView;
    private MediaController mediaControls;
    private int videoPosition = 0;
    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        activity = this;    //PER CHIUDERE L'ACTIVITY DOPO AVER SALVATO LA NOTA PER NON AVERE PROBLEMI DI UP NAVIGATION

        if (savedInstanceState != null) {
            note = savedInstanceState.getParcelable(KEY_NOTE);
            isImageFitToScreen = savedInstanceState.getBoolean(KEY_IMAGE);
        } else {
            Intent intent = getIntent();
            Bundle receivedBundle = intent.getExtras();

            if (receivedBundle != null) {
                note = receivedBundle.getParcelable(KEY_NOTE);
            }

        }


        getSupportActionBar().setTitle(note.getTitle());

        text = (TextView) findViewById(R.id.noteText);
        text.setText(Html.fromHtml(note.getText()));

        date = (TextView) findViewById(R.id.note_date);
        date.setText(DateFormat.getDateTimeInstance().format(note.getDate()));

        //attached = (ImageView) findViewById(R.id.mediaView);


        scrollView = (ScrollView) findViewById(R.id.scroll);

        imageNote = (ImageView) findViewById(R.id.image_note);
        audioPlayer=(ViewGroup)findViewById(R.id.audio_container);
        mediaContainer = (RelativeLayout) findViewById(R.id.media_container);
        mediaContainer.getBackground().setAlpha(0);

        try {
            audio = FileManager.init(this)
                    .with(note)
                    .get(FileManager.TYPE_AUDIO);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            image = FileManager.init(this)
                    .with(note)
                    .get(FileManager.TYPE_IMAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            video = FileManager.init(this)
                    .with(note)
                    .get(FileManager.TYPE_VIDEO);
        } catch (Exception e) {
            e.printStackTrace();
        }


        BottomBarTab[] tabs = new BottomBarTab[4];

        tabs[0] = new BottomBarTab(R.drawable.ic_reorder_white_24dp, R.string.bottom_bar_text_title);

        int index = 1;

        if (audio != null && audio.toString().length() > 0) {
            tabs[index] = new BottomBarTab(R.drawable.ic_keyboard_voice_white_24dp, R.string.bottom_bar_audio_title);
            audioIndex=index;
            index++;
        }
        if (image != null && image.toString().length() > 0) {
            tabs[index] = new BottomBarTab(R.drawable.ic_image_white_24dp, R.string.bottom_bar_image_title);
            imageIndex=index;
            index++;
        }
        if (video != null && video.toString().length() > 0) {
            tabs[index] = new BottomBarTab(R.drawable.ic_local_movies_white_24dp, R.string.bottom_bar_video_title);
            videoIndex=index;
            index++;
        }

        BottomBarTab[] defintiveTabs = new BottomBarTab[index];

        System.arraycopy(tabs, 0, defintiveTabs, 0, index);

        if (audio.toString().length() > 0 || video.toString().length() > 0 || image.toString().length() > 0) {

            bottomBar = BottomBar.attach(this, savedInstanceState);
            bottomBar.noTopOffset();
            bottomBar.noNavBarGoodness();
            bottomBar.setMaxFixedTabs(index-1);
            bottomBar.setItems(defintiveTabs);
            for(int i = 0; i<index;i++){
                bottomBar.mapColorForTab(i, ContextCompat.getColor(this, R.color.colorAccent));
            }

            bottomBar.setOnTabClickListener(new OnTabClickListener() {
                @Override
                public void onTabSelected(int position) {
                    if(position==0){
                        mediaContainer.getBackground().setAlpha(0);
                        clearLayout();

                    }
                    if (position==audioIndex){
                        mediaContainer.getBackground().setAlpha(200);
                        clearLayout();

                        AudioWife.getInstance()
                                .init(getApplicationContext(), Uri.parse(audio.getAbsolutePath()))
                                .useDefaultUi(audioPlayer, getLayoutInflater());
                    }
                    if (position==imageIndex){
                        mediaContainer.getBackground().setAlpha(200);
                        clearLayout();

                        if(imageNote == null)
                            imageNote = (ImageView) findViewById(R.id.image_note);
                        imageNote.setVisibility(View.VISIBLE);



                       imageNote.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                               fullScreenPhoto();




                            }
                        });
                        try {
                            Glide
                                    .with(getApplicationContext())
                                    .load(FileManager
                                            .init(getApplicationContext())
                                            .with(note)
                                            .get(FileManager.TYPE_IMAGE))
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
                                    .into(imageNote);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (position==videoIndex){
                        mediaContainer.getBackground().setAlpha(200);
                        clearLayout();

                        //set the media controller buttons
                        if (mediaControls == null) {
                            mediaControls = new MediaController(NoteDetailActivity.this);
                        }

                        //initialize the VideoView
                        if(myVideoView == null)
                            myVideoView = (VideoView) findViewById(R.id.video_note);
                        myVideoView.setVisibility(View.VISIBLE);



                        // create a progress bar while the video file is loading
                        progressDialog = new ProgressDialog(NoteDetailActivity.this);
                        // set a title for the progress bar
                        progressDialog.setTitle("Loading Video");
                        // set a message for the progress bar
                        progressDialog.setMessage("Loading...");
                        //set the progress bar not cancelable on users' touch
                        progressDialog.setCancelable(false);
                        // show the progress bar
                        progressDialog.show();

                        try {
                            Log.i(TAG, "Trying");
                            //set the media controller in the VideoView
                            myVideoView.setMediaController(mediaControls);
                            //set the uri of the video to be played
                            //myVideoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.developers));
                            //myVideoView.setVideoPath("http://www.ebookfrenzy.com/android_book/movie.mp4");
                            if(!myVideoView.canPause()) {
                                myVideoView.setVideoURI(Uri.parse(FileManager
                                        .init(getApplicationContext())
                                        .with(note)
                                        .get(FileManager.TYPE_VIDEO).getPath()));
                            }

                        } catch (Exception e) {
                            Log.e("Error", e.getMessage());
                            e.printStackTrace();
                        }
                        myVideoView.requestFocus();
                        //we also set an setOnPreparedListener in order to know when the video file is ready for playback
                        myVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                            @Override
                            public void onPrepared(MediaPlayer mediaPlayer) {
                                // close the progress bar and play the video
                                progressDialog.dismiss();
                                //if we have a position on savedInstanceState, the video playback should start from here
                                myVideoView.seekTo(videoPosition);
                                if (videoPosition == 0) {
//                                    myVideoView.start();
                                } else {
                                    //if we come from a resumed activity, video playback will be paused
                                    myVideoView.pause();
                                }
                            }

                        });

                    }
                }

                @Override
                public void onTabReSelected(int position) {

                }
            });
        }

        Log.i(TAG, "onCreate: audio: " + audio.toString());
        Log.i(TAG, "onCreate: video: " + video.toString());
        Log.i(TAG, "onCreate: foto: " + image.toString());

    }

    private void clearLayout() {
        AudioWife.getInstance().release();
        audioPlayer.removeAllViewsInLayout();
        if(myVideoView != null) myVideoView.setVisibility(View.INVISIBLE);
        if(imageNote != null) imageNote.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(imageNote != null) imageNote.setImageResource(android.R.color.transparent);
        if(bottomBar != null) bottomBar.selectTabAtPosition(0, false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.goToEdit:

                Intent i = new Intent(getApplicationContext(), EditNoteActivity.class);
                i.putExtra(NoteDetailActivity.KEY_NOTE, note);
                startActivity(i);

                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_menu, menu);

        return true;
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_NOTE, note);
        outState.putBoolean(KEY_IMAGE, isImageFitToScreen);
        if (bottomBar!=null)
            bottomBar.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        note = null;
    }


    private void fullScreenPhoto(){
        // BEGIN_INCLUDE (get_current_ui_flags)
        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        // END_INCLUDE (get_current_ui_flags)
        // BEGIN_INCLUDE (toggle_ui_flags)
        boolean isImmersiveModeEnabled =
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        if (isImmersiveModeEnabled) {
            Log.i(TAG, "Turning immersive mode mode off. ");
            bottomBar.show();
        } else {
            Log.i(TAG, "Turning immersive mode mode on.");
            bottomBar.hide();
        }
/*
        // Navigation bar hiding:  Backwards compatible to ICS.
        if (Build.VERSION.SDK_INT >= 14) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }
*/
        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        // Immersive mode: Backward compatible to KitKat.
        // Note that this flag doesn't do anything by itself, it only augments the behavior
        // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
        // all three flags are being toggled together.
        // Note that there are two immersive mode UI flags, one of which is referred to as "sticky".
        // Sticky immersive mode differs in that it makes the navigation and status bars
        // semi-transparent, and the UI flag does not get cleared when the user interacts with
        // the screen.
        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
        //END_INCLUDE (set_ui_flags)
    }


}
