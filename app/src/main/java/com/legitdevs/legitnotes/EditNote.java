package com.legitdevs.legitnotes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;

public class EditNote extends AppCompatActivity {

    private EditText title;
    private EditText text;
    private Note note;
    private TextView date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        title = (EditText) findViewById(R.id.editTitle);
        text = (EditText) findViewById(R.id.editText);
        date=(TextView)findViewById(R.id.creationDate);

        Intent intent = getIntent();
        Bundle receivedBundle = intent.getExtras();

        if (receivedBundle!=null){
            note = receivedBundle.getParcelable(DetailNotes.KEY_NOTE);
            title.setText(note.getTitle());
            text.setText(note.getText());
            date.setText(DateFormat.getDateTimeInstance().format(note.getDate()));
        }


    }
}
