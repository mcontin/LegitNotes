package com.legitdevs.legitnotes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;
import android.widget.MediaController;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.legitdevs.legitnotes.filemanager.FileManager;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarBadge;
import com.roughike.bottombar.BottomBarTab;
import com.roughike.bottombar.OnMenuTabClickListener;
import com.roughike.bottombar.OnTabClickListener;

import android.util.Log;

import java.io.File;

public class NoteDetailActivity extends AppCompatActivity implements ObservableScrollViewCallbacks {

    public final static String KEY_NOTE = "note";
    public final static String KEY_IMAGE = "image";

    public static NoteDetailActivity activity;

    private Note note;
    private ObservableScrollView scrollView;
    private ImageView imageNote;
    private TextView title;
    private TextView text;
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

        TextView text = (TextView) findViewById(R.id.noteText);
        text.setText(note.getText());

        //attached = (ImageView) findViewById(R.id.mediaView);


        scrollView = (ObservableScrollView) findViewById(R.id.scroll);
        scrollView.setScrollViewCallbacks(this);

        imageNote = (ImageView) findViewById(R.id.image_note);
        audioPlayer=(ViewGroup)findViewById(R.id.audio_container);
        mediaContainer = (RelativeLayout) findViewById(R.id.media_container);
        mediaContainer.getBackground().setAlpha(0);

        audio = FileManager.init(this)
                .with(note)
                .get(FileManager.TYPE_AUDIO);

        image = FileManager.init(this)
                .with(note)
                .get(FileManager.TYPE_IMAGE);

        video = FileManager.init(this)
                .with(note)
                .get(FileManager.TYPE_VIDEO);


        BottomBarTab[] tabs = new BottomBarTab[4];

        tabs[0] = new BottomBarTab(R.drawable.ic_reorder_white_24dp, R.string.bottom_bar_text_title);

        int index = 1;

        if (audio != null) {
            tabs[index] = new BottomBarTab(R.drawable.ic_keyboard_voice, R.string.bottom_bar_audio_title);
            audioIndex=index;
            index++;
        }
        if (image != null) {
            tabs[index] = new BottomBarTab(R.drawable.ic_image_white_24dp, R.string.bottom_bar_image_title);
            imageIndex=index;
            index++;
        }
        if (video != null) {
            tabs[index] = new BottomBarTab(R.drawable.ic_local_movies_white_24dp, R.string.bottom_bar_video_title);
            videoIndex=index;
            index++;
        }

        BottomBarTab[] defintiveTabs = new BottomBarTab[index];

        System.arraycopy(tabs, 0, defintiveTabs, 0, index);


        if (audio != null || image != null || video != null) {

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
                        AudioWife.getInstance().release();
                        audioPlayer.removeAllViewsInLayout();
                    }
                    if (position==audioIndex){
                        mediaContainer.getBackground().setAlpha(180);
                        AudioWife.getInstance()
                                .init(getApplicationContext(), Uri.parse(audio.getAbsolutePath()))
                                .useDefaultUi(audioPlayer, getLayoutInflater());
                    }
                    if (position==imageIndex){
                        mediaContainer.getBackground().setAlpha(180);
                        AudioWife.getInstance().release();
                        audioPlayer.removeAllViewsInLayout();

                    }
                    if (position==videoIndex){
                        mediaContainer.getBackground().setAlpha(180);
                        AudioWife.getInstance().release();
                        audioPlayer.removeAllViewsInLayout();

                        //set the media controller buttons
                        if (mediaControls == null) {
                            mediaControls = new MediaController(NoteDetailActivity.this);
                        }

                        //initialize the VideoView
                        myVideoView = (VideoView) findViewById(R.id.video_note);

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
                            //set the media controller in the VideoView
                            myVideoView.setMediaController(mediaControls);
                            //set the uri of the video to be played
                            String videoPath = FileManager.init(getApplicationContext())
                                    .with(note)
                                    .get(FileManager.TYPE_VIDEO)
                                    .toString();

                            myVideoView.setVideoPath(videoPath);
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
                                    myVideoView.start();
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


//        bottomBar = BottomBar.attach(this, savedInstanceState);
//        bottomBar.noTopOffset();
//        bottomBar.noNavBarGoodness();
//        bottomBar.setItemsFromMenu(R.menu.bottombar_menu, new OnMenuTabClickListener() {
//            @Override
//            public void onMenuTabSelected(@IdRes int menuItemId) {
//                switch (menuItemId) {
//
//                    case R.id.bottomBarText:
//                        mediaContainer.getBackground().setAlpha(0);
//                        imageNote.setVisibility(View.GONE);
//
//                        break;
//                    case R.id.bottomBarAudio:
//                        mediaContainer.getBackground().setAlpha(240);
//                        imageNote.setVisibility(View.GONE);
//                        break;
//
//                    case R.id.bottomBarImage:
//                        mediaContainer.getBackground().setAlpha(240);
//                        imageNote.setVisibility(View.VISIBLE);
//                        imageNote.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                if (!isImageFitToScreen) {
//                                    isImageFitToScreen = true;
//                                    bottomBar.show();
//                                } else {
//                                    isImageFitToScreen = false;
//                                    bottomBar.hide();
//                                    imageNote.setScaleType(ImageView.ScaleType.FIT_XY);
//                                }
//                            }
//                        });
//                        break;
//
//                    case R.id.bottomBarVideo:
//                        mediaContainer.getBackground().setAlpha(240);
//                        imageNote.setVisibility(View.GONE);
//                        break;
//                }
//            }
//
//            @Override
//            public void onMenuTabReSelected(@IdRes int menuItemId) {
//                switch (menuItemId) {
//                    case R.id.bottomBarAudio:
//                        break;
//                    case R.id.bottomBarImage:
//                        break;
//                    case R.id.bottomBarVideo:
//                        break;
//                }
//            }
//        });


//        bottomBar.mapColorForTab(0, ContextCompat.getColor(this, R.color.colorAccent));
//        bottomBar.mapColorForTab(1, ContextCompat.getColor(this, R.color.colorAccent));
//        bottomBar.mapColorForTab(2, ContextCompat.getColor(this, R.color.colorAccent));
//        bottomBar.mapColorForTab(3, ContextCompat.getColor(this, R.color.colorAccent));
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
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        // ViewHelper.setTranslationY(attached, scrollY / 4 * 3);

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_NOTE, note);
        outState.putBoolean(KEY_IMAGE, isImageFitToScreen);
        bottomBar.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        note = null;
    }


}
