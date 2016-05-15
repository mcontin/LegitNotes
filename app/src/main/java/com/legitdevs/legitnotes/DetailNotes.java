package com.legitdevs.legitnotes;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.nineoldandroids.view.ViewHelper;

public class DetailNotes extends AppCompatActivity implements ObservableScrollViewCallbacks {

    public final static String KEY_NOTE = "note";

    private ObservableScrollView scrollView;
    private ImageView attached;
    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_notes);

        Intent intent = getIntent();
        Bundle receivedBundle = intent.getExtras();
        note = receivedBundle.getParcelable(KEY_NOTE);

        TextView text = (TextView) findViewById(R.id.noteText);
        text.setText(note.getText());

        scrollView = (ObservableScrollView) findViewById(R.id.scroll);
        scrollView.setScrollViewCallbacks(this);

        attached = (ImageView) findViewById(R.id.mediaView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO link per modificare la nota
            }
        });

    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        ViewHelper.setTranslationY(attached, scrollY / 4 * 3);

    }

    @Override
    public void onDownMotionEvent() {

    }
    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }
}
