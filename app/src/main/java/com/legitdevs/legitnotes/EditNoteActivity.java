package com.legitdevs.legitnotes;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.legitdevs.legitnotes.database.DatabaseManager;

import java.text.DateFormat;
import java.util.Date;

import jp.wasabeef.richeditor.RichEditor;

public class EditNoteActivity extends AppCompatActivity {

    private EditText title;
    private RichEditor text;
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
        text = (RichEditor) findViewById(R.id.editText);
        date = (TextView) findViewById(R.id.creationDate);
        database = new DatabaseManager(this);
        current = new Date();

        Intent intent = getIntent();
        Bundle receivedBundle = intent.getExtras();

        if (receivedBundle != null) {
            note = receivedBundle.getParcelable(NoteDetailActivity.KEY_NOTE);
            title.setText(note.getTitle());
            text.setHtml(note.getText());
            date.setText(DateFormat.getDateTimeInstance().format(note.getDate()));
            media = note.getMedia();
        } else {
            note = new Note();
            date.setText(DateFormat.getDateTimeInstance().format(current));
        }
        text.setPadding(10, 10, 10, 10);
        text.setPlaceholder("" + R.string.new_text);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text.focusEditor();
            }
        });

        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                note.setTitle(title.getText().toString());
                note.setText(text.getHtml().toString());
                database.addNote(note);
            }
        });

        findViewById(R.id.add_media).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                media += "";
                note.setMedia(media);
            }
        });

        findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text.setBold();
            }
        });

        findViewById(R.id.action_italic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text.setItalic();
            }
        });

        findViewById(R.id.action_subscript).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text.setSubscript();
            }
        });

        findViewById(R.id.action_strikethrough).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text.setStrikeThrough();
            }
        });

        findViewById(R.id.action_underline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text.setUnderline();
            }
        });

        findViewById(R.id.action_txt_color).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override
            public void onClick(View v) {
                text.setTextColor(isChanged ? Color.BLACK : Color.RED);
                isChanged = !isChanged;
            }
        });

        findViewById(R.id.action_bg_color).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override
            public void onClick(View v) {
                text.setTextBackgroundColor(isChanged ? Color.TRANSPARENT : Color.YELLOW);
                isChanged = !isChanged;
            }
        });

        findViewById(R.id.action_insert_checkbox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text.insertTodo();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.save_note) {

            DatabaseManager database = new DatabaseManager(this);

            //TODO setters prendendo dagli edit text
            note.setTitle("");
            note.setText("");
            //...

            database.addNote(note);

            Intent intent = new Intent(this, NoteDetailActivity.class);
            intent.putExtra(NoteDetailActivity.KEY_NOTE, note);
            startActivity(intent);
            NoteDetailActivity.activity.finish();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.editor, menu);

        return true;
    }
}