package com.legitdevs.legitnotes;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.legitdevs.legitnotes.database.DatabaseManager;
import com.legitdevs.legitnotes.filemanager.FileManager;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;

public class EditNoteActivity extends AppCompatActivity
        implements IDeletionListener, IMediaSaver,
        LocationListener, AudioInsideNoteDialog.IDirAudioNote,
        ConfirmRemovalMediasDialog.IDeleteMedia {

    private static final String KEY_FILE_PHOTO = "photofile";
    private static final String KEY_FILE_AUDIO = "audiofile";
    private static final String KEY_FILE_VIDEO = "videofile";

    private static final String TAG = "EditNoteActivity";
    private static final String DIALOG = "start dialog";
    private static final String KEY_NOTE = "note";
    private static final String KEY_POSITION = "position";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_GALLERY = 2;
    private static final int REQUEST_VIDEO_CAPTURE = 3;

    //private RichEditor text;
    private Note note;
    private TextView date;
    private FloatingActionButton fabGallery, fabPhoto, fabAudio, fabVideo, fabLocation;

    private File photoFile, audioFile, videoFile;
    private Uri photoUri;
    private Bitmap photoBitmap;
    private EditText title;
    private EditText text;
    private LocationManager locationManager;
    private ImageView deleteAudio, deleteVideo, deleteImage, previewImage, previewVideo;
    public FrameLayout previewAudio;
    private LinearLayout containerAudio, containerImage, containerVideo;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(KEY_FILE_VIDEO, videoFile.getAbsolutePath());
        outState.putString(KEY_FILE_AUDIO, audioFile.getAbsolutePath());
        outState.putString(KEY_FILE_PHOTO, photoFile.getAbsolutePath());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        if(savedInstanceState != null) {
            if(savedInstanceState.getString(KEY_FILE_PHOTO) != null)
                photoFile = new File(savedInstanceState.getString(KEY_FILE_PHOTO));
            if(savedInstanceState.getString(KEY_FILE_AUDIO) != null)
                audioFile = new File(savedInstanceState.getString(KEY_FILE_AUDIO));
            if(savedInstanceState.getString(KEY_FILE_VIDEO) != null)
                videoFile = new File(savedInstanceState.getString(KEY_FILE_VIDEO));
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        title = (EditText) findViewById(R.id.editTitle);
        text = (EditText) findViewById(R.id.editText);
        //text = (RichEditor) findViewById(R.id.editText);
        date = (TextView) findViewById(R.id.creationDate);

        Intent intent = getIntent();
        Bundle receivedBundle = intent.getExtras();

        if (receivedBundle != null) {
            note = receivedBundle.getParcelable(NoteDetailActivity.KEY_NOTE);
        }
        if (note == null) {
            note = new Note();
        }

        title.setText(note.getTitle());
        text.setText(note.getText());
        date.setText(DateFormat.getDateTimeInstance().format(note.getDate()));

        containerAudio = (LinearLayout) findViewById(R.id.container_audio);
        deleteAudio = (ImageView) findViewById(R.id.delete_audio);
        deleteAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmRemovalMediasDialog.getInstance(FileManager.TYPE_AUDIO).show(getSupportFragmentManager(),DIALOG);
            }
        });
        previewAudio = (FrameLayout) findViewById(R.id.preview_audio);

        containerImage = (LinearLayout) findViewById(R.id.container_image);
        deleteImage = (ImageView) findViewById(R.id.delete_image);
        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmRemovalMediasDialog.getInstance(FileManager.TYPE_IMAGE).show(getSupportFragmentManager(),DIALOG);
            }
        });
        previewImage = (ImageView) findViewById(R.id.preview_image);

        containerVideo = (LinearLayout) findViewById(R.id.container_video);
        deleteVideo = (ImageView) findViewById(R.id.delete_video);
        deleteVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmRemovalMediasDialog.getInstance(FileManager.TYPE_VIDEO).show(getSupportFragmentManager(),DIALOG);
            }
        });
        previewVideo = (ImageView) findViewById(R.id.preview_video);

        //TODO togliere tutti i casi in cui i file possano essere null per gestire più facilmente il ciclo di vita
        try {
            audioFile = FileManager.init(this)
                    .with(note)
                    .get(FileManager.TYPE_AUDIO);
            videoFile = FileManager.init(this)
                    .with(note)
                    .get(FileManager.TYPE_VIDEO);
            photoFile = FileManager.init(this)
                    .with(note)
                    .get(FileManager.TYPE_IMAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        showAudioPreview(audioFile != null && audioFile.length() > 0);
        showVideoPreview(videoFile != null && videoFile.length() > 0);
        showImagePreview(photoFile != null && photoFile.length() > 0);

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

                fabGallery = (FloatingActionButton) findViewById(R.id.fab_from_gallery);
                fabGallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //call the system's gallery
                        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        getIntent.setType("image/*");

                        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        pickIntent.setType("image/*");

                        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

                        startActivityForResult(chooserIntent, REQUEST_IMAGE_GALLERY);

                        fabMenu.collapse();
                    }
                });

                fabPhoto = (FloatingActionButton) findViewById(R.id.fab_photo);
                fabPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //take a picture
                        dispatchTakePictureIntent();
                        fabMenu.collapse();
                    }
                });

                fabAudio = (FloatingActionButton) findViewById(R.id.fab_audio);
                fabAudio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //record audio
                        AudioInsideNoteDialog.getInstance().show(getSupportFragmentManager(), DIALOG);
                        fabMenu.collapse();
                    }
                });

                fabVideo = (FloatingActionButton) findViewById(R.id.fab_video);
                fabVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //record a video
                        dispatchTakeVideoIntent();
                        fabMenu.collapse();
                    }
                });

                fabLocation = (FloatingActionButton) findViewById(R.id.fab_position);
                fabLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        setUserLocation();
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

    @Override
    public void getDirAudio(File dir) {
        audioFile = dir;

        showAudioPreview(audioFile != null && audioFile.length() > 0);

    }

    @Override
    public void onLocationChanged(Location location) throws SecurityException {
        //setto la posizione dell'utente ogni volta che apre il fragment per consentire alle card di scrivere la distanza
        note.setPosition(location);
        locationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void setUserLocation() {
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        try {
            locationManager.requestLocationUpdates(locationProvider, 0, 0, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }

    private File createImageFile() throws IOException {
        File internalMemory = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                "image",        /* prefix */
                ".jpg",         /* suffix */
                internalMemory  /* directory */
        );

        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                // Error occurred while creating the File
                e.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoUri = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        photoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
            previewAudio.removeAllViewsInLayout();
        }
    }

    //open video
    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    private void showImagePreview(boolean show) {
        if(show) {
            containerImage.setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext())
                    .load(photoFile)
                    .centerCrop()
                    .into(previewImage);
        }
    }
    private void hideImagePreview() {
        containerImage.setVisibility(View.GONE);
    }
    private void showAudioPreview(boolean show) {
        if(show) {
            hideAudioPreview();

            containerAudio.setVisibility(View.VISIBLE);
            AudioWife.getInstance()
                    .init(this, Uri.parse(audioFile.getAbsolutePath()))
                    .useDefaultUi(previewAudio, getLayoutInflater());
            AudioWife.getInstance().checkMediaPlayer();
        }
    }
    private void hideAudioPreview() {
        AudioWife.getInstance().release();
        previewAudio.removeAllViewsInLayout();
        containerAudio.setVisibility(View.GONE);
    }
    private void showVideoPreview(boolean show) {
        if (show) {
            containerVideo.setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext())
                    .load(videoFile)
                    .centerCrop()
                    .into(previewVideo);
        }
    }
    private void hideVideoPreview() {
        containerVideo.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri videoUri = data.getData();
            videoFile = new File(getRealPathFromURI(videoUri));

            showVideoPreview(true);
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            showImagePreview(true);
        }

    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public void removeMedia(String file){

        switch (file){
            case FileManager.TYPE_AUDIO:
                audioFile = null;
                hideAudioPreview();
                break;
            case FileManager.TYPE_IMAGE:
                photoFile = null;
                hideImagePreview();
                break;
            case FileManager.TYPE_VIDEO:
                videoFile = null;
                hideVideoPreview();
                break;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.save_note:
                saveChanges();
                break;

            case R.id.delete_note:
                Bundle bundle = new Bundle();
                bundle.putParcelable(KEY_NOTE, note);
                bundle.putInt(KEY_POSITION, -1); //non serve la posizione in questa activity
                ConfirmRemovalDialog.getInstance(bundle).show(getSupportFragmentManager(), "dialog");
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNoteDeleted(int position) {
        Intent intent = new Intent(this, HomeActivity.class);
        if (HomeActivity.activity != null)
            HomeActivity.activity.finish();
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.editor, menu);

        return true;
    }

    private void saveChanges() {
        note.setTitle(title.getText().toString());
        note.setText(text.getText().toString());
        //note.setMedia(media);

        if (audioFile == null) {
            //remove from note and files
            FileManager.init(this)
                    .with(note)
                    .delete(FileManager.TYPE_AUDIO);
        } else {
            //savemedia
            saveMedia(FileManager.TYPE_AUDIO, audioFile);
        }

        if (photoFile == null) {
            //remove from note and files
            FileManager.init(this)
                    .with(note)
                    .delete(FileManager.TYPE_IMAGE);
        } else {
            //savemedia
            saveMedia(FileManager.TYPE_IMAGE, photoFile);
        }

        if (videoFile == null) {
            //remove from note and files
            FileManager.init(this)
                    .with(note)
                    .delete(FileManager.TYPE_VIDEO);
        } else {
            //savemedia
            saveMedia(FileManager.TYPE_VIDEO, videoFile);
        }

        DatabaseManager.getInstance(this).saveNote(note);

        //nota modificata, devo killare l'activity di dettaglio precedente
        if (NoteDetailActivity.activity != null)
            NoteDetailActivity.activity.finish();

        finish();

        Toast.makeText(getApplicationContext(), R.string.save_note_toast, Toast.LENGTH_LONG).show();
    }

    @Override
    public void saveMedia(String fileType, File fileName) {
        FileManager.init(this)
                .with(note)
                .save(fileType, fileName);
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