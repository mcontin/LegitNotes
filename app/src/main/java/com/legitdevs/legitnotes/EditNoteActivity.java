package com.legitdevs.legitnotes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.legitdevs.legitnotes.database.DatabaseManager;

import java.io.File;
import java.text.DateFormat;
import java.util.HashMap;

public class EditNoteActivity extends AppCompatActivity
    implements IDeletionListener{

    private static final String KEY_NOTE = "note";
    private static final String KEY_POSITION = "position";
    private EditText title;
    private EditText text;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_VIDEO_CAPTURE = 2;


    //private RichEditor text;
    private Note note;
    private TextView date;
    private HashMap<String, File> medias;
    private FloatingActionButton FABQuickNote, FABNewNote, FABNewAudioNote, FABVideo, FABLocation;
    public static final String DIALOG = "start dialog";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        title = (EditText) findViewById(R.id.editTitle);
        text = (EditText) findViewById(R.id.editText);

        //text = (RichEditor) findViewById(R.id.editText);
        date = (TextView) findViewById(R.id.creationDate);

        Intent intent = getIntent();
        Bundle receivedBundle = intent.getExtras();

        if (receivedBundle != null) {
            note = receivedBundle.getParcelable(NoteDetailActivity.KEY_NOTE);
        } else {
            note = new Note();
        }

        title.setText(note.getTitle());
        text.setText(note.getText());
        date.setText(DateFormat.getDateTimeInstance().format(note.getDate()));
        medias = note.getMedias();




        //View newView = new View();

        final FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frame_layout_insert_media);
        assert frameLayout != null;
        frameLayout.getBackground().setAlpha(0);
        final FloatingActionsMenu fabMenu = (FloatingActionsMenu) findViewById(R.id.fab_menu_insert);
        assert fabMenu != null;
        fabMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                frameLayout.getBackground().setAlpha(200);
                frameLayout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        fabMenu.collapse();
                        return true;
                    }
                });

                FABQuickNote = (FloatingActionButton) findViewById(R.id.new_audio);
                FABQuickNote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //insert funcion that calls the registaration
                        AudioNoteDialog.getInstance().show(getSupportFragmentManager(), DIALOG);
                        fabMenu.collapse();

                    }
                });

                FABNewNote = (FloatingActionButton) findViewById(R.id.fab_new_picture);
                FABNewNote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //call gallery and save the image that was taken
                        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        getIntent.setType("image/*");

                        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        pickIntent.setType("image/*");

                        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                        startActivityForResult(chooserIntent, 1);




                        fabMenu.collapse();

                    }
                });


                FABNewNote = (FloatingActionButton) findViewById(R.id.new_camera_note);
                FABNewNote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // connect with the camera and try to insert the photo just pluged in(obviusily try to ask if you want to retry the photo or not
                        dispatchTakePictureIntent();
                        fabMenu.collapse();

                    }
                });

                FABNewNote = (FloatingActionButton) findViewById(R.id.new_video);
                FABNewNote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //insert the video

                        dispatchTakeVideoIntent();



                        fabMenu.collapse();

                    }
                });

            }

            @Override
            public void onMenuCollapsed() {
                frameLayout.getBackground().setAlpha(0);
                frameLayout.setOnTouchListener(null);
            }
        });

    }


    // open camera
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri videoUri = data.getData();
           // mVideoView.setVideoURI(videoUri);
        }else  if (
                requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
         //   mImageView.setImageBitmap(imageBitmap);
        }


    }

    //open video
    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.save_note:

                saveChanges();

                break;

            case R.id.delete_note:
                Bundle bundle = new Bundle();
                bundle.putParcelable(KEY_NOTE, note);
                bundle.putInt(KEY_POSITION, -1); //non serve la posizione in questa activity
                ConfirmRemovalDialog.getInstance(bundle).show(getSupportFragmentManager(),"dialog");

                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNoteDeleted(int position) {
        Intent intent = new Intent(this, HomeActivity.class);
        if(HomeActivity.activity != null)
            HomeActivity.activity.finish();
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.editor, menu);

        return true;
    }

    private void saveChanges(){
        note.setTitle(title.getText().toString());
        note.setText(text.getText().toString());
        //note.setMedia(media);

        DatabaseManager.getInstance(this).addNote(note);

        //nota modificata, devo killare l'activity di dettaglio precedente
        if(NoteDetailActivity.activity != null)
            NoteDetailActivity.activity.finish();

        finish();

        Toast.makeText(getApplicationContext(),R.string.save_note_toast, Toast.LENGTH_LONG).show();


    }
}

/*text.setPadding(10, 10, 10, 10);
        text.setPlaceholder("" + R.string.new_text);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text.focusEditor();
            }
        });

        /*
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


        findViewById(R.id.action_superscript).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                text.setSuperscript();
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
        });*/