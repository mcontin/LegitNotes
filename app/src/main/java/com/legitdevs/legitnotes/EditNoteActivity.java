package com.legitdevs.legitnotes;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
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
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.legitdevs.legitnotes.database.DatabaseManager;
import com.legitdevs.legitnotes.filemanager.FileManager;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


import android.support.v7.app.AlertDialog;
import android.os.Build;
import android.util.Log;

import yuku.ambilwarna.AmbilWarnaDialog;


public class EditNoteActivity extends AppCompatActivity
        implements IDeletionListener, IMediaSaver, LocationListener,
        AudioInsideNoteDialog.IDirAudioNote, AmbilWarnaDialog.OnAmbilWarnaListener, ConfirmRemovalMediasDialog.IDeleteMedia {

    private static final String KEY_FILE_PHOTO = "photofile";
    private static final String KEY_FILE_AUDIO = "audiofile";
    private static final String KEY_FILE_VIDEO = "videofile";
    private static final String KEY_FABMENU_OPEN = "fabmenu";

    private static final String TAG = "EditNoteActivity";
    private static final String DIALOG = "start dialog";
    private static final String KEY_NOTE = "note";
    private static final String KEY_POSITION = "position";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_GALLERY = 2;
    private static final int REQUEST_VIDEO_CAPTURE = 3;

    private Note note;
    private TextView date;
    private HashMap<String, String> medias;
    private FloatingActionButton fabGallery, fabPhoto, fabAudio, fabVideo, fabLocation;

    private File photoFile, tempPhotoFile, audioFile, videoFile;
    private Uri photoUri;
    private Bitmap photoBitmap;
    private EditText title;
    private CustomEditText text;
    private LocationManager locationManager;
    private ImageView deleteAudio, deleteVideo, deleteImage, previewImage, previewVideo;
    public FrameLayout previewAudio;
    private LinearLayout containerAudio, containerImage, containerVideo;

    private LinearLayout lnl;
    private AmbilWarnaDialog colorPickerDialog;
    private ImageView imgChangeColor;

    private int selectionStart;
    private int selectionEnd;

    private LinearLayout mMediaContainer;

    private FrameLayout frameLayout;
    private FloatingActionsMenu fabMenu;
    private boolean fabMenuOpen = false;

    private CustomEditText.EventBack eventBack = new CustomEditText.EventBack() {

        @Override
        public void close() {
            mMediaContainer.setVisibility(View.VISIBLE);
            lnl.setVisibility(View.GONE);
        }

        @Override
        public void show() {
            mMediaContainer.setVisibility(View.GONE);
            lnl.setVisibility(View.VISIBLE);
        }
    };

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (text.isFocused()) {
                lnl.setVisibility(View.VISIBLE);
                mMediaContainer.setVisibility(View.GONE);
            } else {
                mMediaContainer.setVisibility(View.VISIBLE);
            }
        }
    };


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(KEY_FILE_VIDEO, videoFile.toString());
        outState.putString(KEY_FILE_AUDIO, audioFile.toString());
        outState.putString(KEY_FILE_PHOTO, photoFile.toString());
        outState.putBoolean(KEY_FABMENU_OPEN, fabMenuOpen);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        if(savedInstanceState != null) {
            photoFile = new File(savedInstanceState.getString(KEY_FILE_PHOTO));
            audioFile = new File(savedInstanceState.getString(KEY_FILE_AUDIO));
            videoFile = new File(savedInstanceState.getString(KEY_FILE_VIDEO));
            fabMenuOpen = savedInstanceState.getBoolean(KEY_FABMENU_OPEN);
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        title = (EditText) findViewById(R.id.editTitle);
        text = (CustomEditText) findViewById(R.id.editText);
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
        text.setText(Html.fromHtml(note.getText()));
        date.setText(DateFormat.getDateTimeInstance().format(note.getDate()));
        medias = note.getMedias();
        mMediaContainer = (LinearLayout) findViewById(R.id.media_content);
        containerAudio = (LinearLayout) findViewById(R.id.container_audio);
        deleteAudio = (ImageView) findViewById(R.id.delete_audio);
        deleteAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmRemovalMediasDialog.getInstance(FileManager.TYPE_AUDIO).show(getSupportFragmentManager(), DIALOG);
            }
        });
        previewAudio = (FrameLayout) findViewById(R.id.preview_audio);

        containerImage = (LinearLayout) findViewById(R.id.container_image);
        deleteImage = (ImageView) findViewById(R.id.delete_image);
        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmRemovalMediasDialog.getInstance(FileManager.TYPE_IMAGE).show(getSupportFragmentManager(), DIALOG);
            }
        });
        previewImage = (ImageView) findViewById(R.id.preview_image);

        containerVideo = (LinearLayout) findViewById(R.id.container_video);
        deleteVideo = (ImageView) findViewById(R.id.delete_video);
        deleteVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmRemovalMediasDialog.getInstance(FileManager.TYPE_VIDEO).show(getSupportFragmentManager(), DIALOG);
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

        showAudioPreview(audioFile != null && audioFile.toString().length() > 0);
        showVideoPreview(videoFile != null && videoFile.toString().length() > 0);
        showImagePreview(photoFile != null && photoFile.toString().length() > 0);

        frameLayout = (FrameLayout) findViewById(R.id.frame_layout_insert_media);
        assert frameLayout != null;
        frameLayout.getBackground().setAlpha(0);
        fabMenu = (FloatingActionsMenu) findViewById(R.id.fab_menu_insert);
        assert fabMenu != null;
        setFabMenuOpen(fabMenuOpen);
        setupFabs();
        fabMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                fabMenuOpen = true;
                setFabMenuOpen(fabMenuOpen);
            }

            @Override
            public void onMenuCollapsed() {
                fabMenuOpen = false;
                setFabMenuOpen(fabMenuOpen);
            }

        });
    }

    private void setupFabs() {
//        fabGallery = (FloatingActionButton) findViewById(R.id.fab_from_gallery);
//        fabGallery.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //call the system's gallery
//                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
//                getIntent.setType("image/*");
//
//                Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                pickIntent.setType("image/*");
//
//                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
//                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
//
//                startActivityForResult(chooserIntent, REQUEST_IMAGE_GALLERY);
//
//                fabMenu.collapse();
//            }
//        });

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

//        fabLocation = (FloatingActionButton) findViewById(R.id.fab_position);
//        fabLocation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setUserLocation();
//                fabMenu.collapse();
//
//            }
//        });

        colorPickerDialog = new AmbilWarnaDialog(this, Color.BLACK, this);
        ToggleButton boldToggle = (ToggleButton) findViewById(R.id.btnBold);
        ToggleButton italicsToggle = (ToggleButton) findViewById(R.id.btnItalics);
        ToggleButton underlinedToggle = (ToggleButton) findViewById(R.id.btnUnderline);
        imgChangeColor = (ImageView) findViewById(R.id.btnChangeTextColor);
        lnl = (LinearLayout) findViewById(R.id.lnlAction);
        lnl.setVisibility(View.VISIBLE);

        text.setHint(getResources().getString(R.string.new_text));
        text.setSingleLine(false);
        text.setMinLines(10);
        text.setBoldToggleButton(boldToggle);
        text.setItalicsToggleButton(italicsToggle);
        text.setUnderlineToggleButton(underlinedToggle);
        text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    lnl.setVisibility(View.VISIBLE);
                    mMediaContainer.setVisibility(View.GONE);
                } else {
                    lnl.setVisibility(View.GONE);
                    mMediaContainer.setVisibility(View.VISIBLE);
                }
            }
        });
        text.setEventBack(eventBack);
        text.setOnClickListener(clickListener);
        imgChangeColor.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                selectionStart = text.getSelectionStart();
                selectionEnd = text.getSelectionEnd();
                colorPickerDialog.show();
            }
        });

        if (medias != null) {
            for (int i = 0; i < medias.size(); i++) {

            }
        }
    }

    private String displayLocation() {

        String errorMessage = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        Location location = note.getPosition();

        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException ioException) {
            errorMessage = getString(R.string.service_not_available);
        } catch (IllegalArgumentException illegalArgumentException) {
            errorMessage = getString(R.string.invalid_lat_long_used);
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size() == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found);
            }
            return errorMessage;
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();
            addressFragments.add(address.getAddressLine(0));
            return addressFragments.get(0);
        }
    }

    private void setFabMenuOpen(boolean open) {

        if(open) {
            frameLayout.getBackground().setAlpha(200);
            frameLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    fabMenu.collapse();
                    return true;
                }
            });
            return;
        }

        frameLayout.getBackground().setAlpha(0);
        frameLayout.setOnTouchListener(null);
    }


    @Override
    public void getDirAudio(File dir) {
        audioFile = dir;

        showAudioPreview(audioFile != null && audioFile.length() > 0);

    }

    @Override
    public void onLocationChanged(Location location) throws SecurityException {

        //setto la posizione dell'utente ogni volta che creo una nota
        note.setPosition(location);
        locationManager.removeUpdates(this);

        Handler mainHandler = new Handler(getApplicationContext().getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                String myAddress = displayLocation();
                Log.i("POSITION",myAddress);
                note.setText(note.getText() + "/n Ti trovi in " + myAddress);
            }
        };
        mainHandler.post(myRunnable);
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
        File tempDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//        tempDir = new File(tempDir.getAbsolutePath()
//                + File.separatorChar
//                + ".temp");
        //se non esiste creo la cartella temporanea
        if (!tempDir.exists()) {
            if(!tempDir.mkdir()) {
                //è successo qualcosa di brutto
                AlertDialog.Builder b = new AlertDialog.Builder(this);
                b.setMessage("Error with internal memory! Please restart the app.");
                b.create().show();
                return new File("");
            }
        }

        File imageTemp = File.createTempFile("image",
                ".jpg",
                tempDir);

        return imageTemp;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            tempPhotoFile = null;
            try {
                tempPhotoFile = createImageFile();
            } catch (IOException e) {
                // Error occurred while creating the File
                e.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (tempPhotoFile != null && tempPhotoFile.exists()) {
                photoUri = Uri.fromFile(tempPhotoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        photoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
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
        if (show) {
            containerImage.setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext())
                    .load(photoFile)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
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

        Log.i(TAG, "onActivityResult: foto: " + photoFile);
        Log.i(TAG, "onActivityResult: temp: " + tempPhotoFile);

        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri videoUri = data.getData();
            videoFile = new File(getRealPathFromURI(videoUri));

            showVideoPreview(true);
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            photoFile = tempPhotoFile;
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
        FileManager.init(this)
                .with(note)
                .delete(file);

        switch (file) {
            case FileManager.TYPE_AUDIO:
                audioFile = new File("");
                hideAudioPreview();
                break;
            case FileManager.TYPE_IMAGE:
                photoFile = new File("");
                hideImagePreview();
                break;
            case FileManager.TYPE_VIDEO:
                videoFile = new File("");
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
        note.setText(text.getTextHTML());
        //note.setMedia(media);

        Log.i(TAG, "saveChanges: audio: " + audioFile.toString());
        Log.i(TAG, "saveChanges: video: " + videoFile.toString());
        Log.i(TAG, "saveChanges: photo: " + photoFile.toString());

        if (audioFile == null || audioFile.toString().length() == 0) {
            //remove from note and files
            FileManager.init(this)
                    .with(note)
                    .delete(FileManager.TYPE_AUDIO);
        } else {
            //savemedia
            if(audioFile.getParentFile().getName().equals(".temp"))
                saveMedia(FileManager.TYPE_AUDIO, audioFile);
        }

        if (photoFile == null || photoFile.toString().length() == 0) {
            //remove from note and files
            FileManager.init(this)
                    .with(note)
                    .delete(FileManager.TYPE_IMAGE);
        } else {
            //savemedia
            if(photoFile.getParentFile().getName().equals("Pictures"))
                saveMedia(FileManager.TYPE_IMAGE, photoFile);
        }

        if (videoFile == null || videoFile.toString().length() == 0) {
            //remove from note and files
            FileManager.init(this)
                    .with(note)
                    .delete(FileManager.TYPE_VIDEO);
        } else {
            //savemedia
            if(videoFile.getParentFile().getName().equals("Camera"))
                saveMedia(FileManager.TYPE_VIDEO, videoFile);
        }

        DatabaseManager.getInstance(this).saveNote(note);
        HomeActivity.activity.empty.setVisibility(View.GONE);
        HomeActivity.activity.orderCards(HomeActivity.activity.chosenItem);

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

    @Override
    public void onCancel(AmbilWarnaDialog dialog) {

    }

    @Override
    public void onOk(AmbilWarnaDialog dialog, int color) {
        text.setColor(color, selectionStart, selectionEnd);
        imgChangeColor.setBackgroundColor(color);
    }

}