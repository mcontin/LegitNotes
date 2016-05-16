package com.legitdevs.legitnotes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.legitdevs.legitnotes.database.DatabaseManager;

import java.text.DateFormat;
import java.util.Date;

public class EditNote extends AppCompatActivity {

    private EditText title;
    private EditText text;
    private Note note;
    private TextView date;
    private Date current;
    private DatabaseManager database;
    private String media;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        title = (EditText) findViewById(R.id.editTitle);
        text = (EditText) findViewById(R.id.editText);
        date = (TextView) findViewById(R.id.creationDate);
        database = new DatabaseManager(this);

        Intent intent = getIntent();
        Bundle receivedBundle = intent.getExtras();

        if (receivedBundle != null) {
            note = receivedBundle.getParcelable(NoteDetailActivity.KEY_NOTE);
            title.setText(note.getTitle());
            text.setText(note.getText());
            date.setText(DateFormat.getDateTimeInstance().format(note.getDate()));
            media=note.getMedia();
        } else {
            note = new Note();
            current = new Date();
            note.setDate(current);
            date.setText(DateFormat.getDateTimeInstance().format(current));
        }
        Button save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                note.setTitle(title.getText().toString());
                note.setText(text.getText().toString());
                /*if (note.getId() != 0) {
                    database.updateNote();
                } else {
                    database.saveNote();
                }*/
            }
        });
        Button add_media=(Button)findViewById(R.id.add_media);
        add_media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                media+="";
                note.setMedia(media);
            }
        });
    }
}
