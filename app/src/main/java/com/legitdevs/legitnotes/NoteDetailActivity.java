package com.legitdevs.legitnotes;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

public class NoteDetailActivity extends AppCompatActivity implements ObservableScrollViewCallbacks {

    public final static String KEY_NOTE = "note";

    public static NoteDetailActivity activity;

    private Note note;
    private ObservableScrollView scrollView;
    private ImageView imageNote;
    private TextView title;
    private TextView text;
    private BottomBar bottomBar;
    private boolean isImageFitToScreen=true;
    private RelativeLayout mediaContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        activity = this;    //PER CHIUDERE L'ACTIVITY DOPO AVER SALVATO LA NOTA PER NON AVERE PROBLEMI DI UP NAVIGATION

        if(savedInstanceState != null) {
            note = savedInstanceState.getParcelable(KEY_NOTE);
        } else {
            Intent intent = getIntent();
            Bundle receivedBundle = intent.getExtras();

            if(receivedBundle != null) {
                note = receivedBundle.getParcelable(KEY_NOTE);
            }
            
        }

        getSupportActionBar().setTitle(note.getTitle());

        TextView text = (TextView) findViewById(R.id.noteText);
        text.setText(note.getText());

        //attached = (ImageView) findViewById(R.id.mediaView);


        scrollView = (ObservableScrollView) findViewById(R.id.scroll);
        scrollView.setScrollViewCallbacks(this);

        imageNote=(ImageView)findViewById(R.id.image_note);
        mediaContainer=(RelativeLayout)findViewById(R.id.media_container);

        mediaContainer.getBackground().setAlpha(0);


        bottomBar = BottomBar.attach(this, savedInstanceState);
        bottomBar.setItemsFromMenu(R.menu.bottombar_menu, new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {
                switch (menuItemId){
                    case R.id.bottomBarAudio:
                        break;
                    case R.id.bottomBarImage:
                        mediaContainer.getBackground().setAlpha(240);
                        imageNote.setVisibility(View.VISIBLE);
                        imageNote.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(isImageFitToScreen) {
                                    isImageFitToScreen=false;
                                    getSupportActionBar().show();
                                    bottomBar.show();
                                }else{
                                    isImageFitToScreen=true;
                                    getSupportActionBar().hide();
                                    bottomBar.hide();
                                    imageNote.setScaleType(ImageView.ScaleType.FIT_XY);
                                }

                            }
                        });

                        break;
                    case R.id.bottomBarVideo:
                        break;
                }
            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {
                switch (menuItemId){
                    case R.id.bottomBarAudio:
                        break;
                    case R.id.bottomBarImage:
                        break;
                    case R.id.bottomBarVideo:
                        break;
                }
            }
        });


        bottomBar.mapColorForTab(0, ContextCompat.getColor(this, R.color.colorAccent));
        bottomBar.mapColorForTab(1, ContextCompat.getColor(this, R.color.colorAccent));
        bottomBar.mapColorForTab(2, ContextCompat.getColor(this, R.color.colorAccent));
        bottomBar.mapColorForTab(3, ContextCompat.getColor(this, R.color.colorAccent));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
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
        bottomBar.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        note = null;
    }



}
