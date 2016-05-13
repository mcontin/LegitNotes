package com.legitdevs.legitnotes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class DetailNotes extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_notes);

        ImageView attached=(ImageView)findViewById(R.id.mediaView);

        Glide
                .with(DetailNotes.this)
                .load("placeholdit.imgix.net/~text?txtsize=33&txt=350%C3%97150&w=350&h=150")
                .into(attached);



    }
}
