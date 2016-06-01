package com.legitdevs.legitnotes;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;

import com.legitdevs.legitnotes.database.DatabaseManager;
import com.legitdevs.legitnotes.filemanager.FileManager;
import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;

import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class HomeActivity extends AppCompatActivity
        implements SearchView.OnQueryTextListener,
        IDeletionListener, IMediaSaver, OrderDialog.ISelectedItem,
        ChangeViewCardsDialog.ISelectedItem {

    private static final String DIALOG_QUICK = "quick";
    private static final String DIALOG_CONFIRM = "confirm";
    private static final String DIALOG_AUDIO = "audio";
    private static final String DIALOG_SETTINGS = "settings";
    private static final String TAG = "HomeActivity";

    private static final int REQUEST_PERMISSION_LOCATION = 1;
    private static final int REQUEST_PERMISSION_WRITE_STORAGE = 2;
    private static final int REQUEST_PERMISSION_MICROPHONE = 3;
    private static final int REQUEST_ALL = 4;

    private static final String KEY_FABMENU_STATE = "fabmenustate";
    private static final String KEY_CHOSEN_ITEM = "chosen";
    private static final String KEY_CHOSEN_COLUMN = "column";

    private RecyclerView recyclerView;
    private NotesAdapter adapter;
    private ArrayList<Note> notes;
    private FloatingActionButton FABQuickNote, FABNewNote, FABNewAudioNote, FABVideo, FABLocation;

    private SearchView searchView;
    private MenuItem searchMenuItem;

    public static HomeActivity activity;

    private FrameLayout frameLayout;
    private FloatingActionsMenu fabMenu;
    private boolean fabMenuOpen = false;
    public ImageView empty;

    public int chosenItem = 2, chosenColumn = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;

        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_home_activity);
        setSupportActionBar(toolbar);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestAllPermissions();
        }

        if (savedInstanceState != null) {
            fabMenuOpen = savedInstanceState.getBoolean(KEY_FABMENU_STATE);
            chosenItem = savedInstanceState.getInt(KEY_CHOSEN_ITEM);
            chosenColumn = savedInstanceState.getInt(KEY_CHOSEN_COLUMN);
        }

        notes = DatabaseManager.getInstance(this).getNotes();
        orderCards(chosenItem);

        empty = (ImageView) findViewById(R.id.empty);

        if (notes.size() == 0) empty.setVisibility(View.VISIBLE);

        //FAB creazione note
        frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        assert frameLayout != null;
        frameLayout.getBackground().setAlpha(0);
        fabMenu = (FloatingActionsMenu) findViewById(R.id.fab_menu);
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

        //lista note
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new NotesAdapter(notes, this);    //adapter per la lista di note e creazione delle Card
        //layout a 2 colonne

        changeCardView(chosenColumn);


    }

    private void setupFabs() {
        FABQuickNote = (FloatingActionButton) findViewById(R.id.fab_quick_note);
        FABQuickNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                QuickNoteDialog.getInstance().show(getSupportFragmentManager(), DIALOG_QUICK);
                fabMenu.collapse();

            }
        });

        FABNewNote = (FloatingActionButton) findViewById(R.id.fab_new_note);
        FABNewNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getBaseContext(), EditNoteActivity.class);
                startActivity(i);
                fabMenu.collapse();

            }
        });

        FABNewAudioNote = (FloatingActionButton) findViewById(R.id.fab_new_audio_note);
        FABNewAudioNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AudioNoteDialog.getInstance().show(getSupportFragmentManager(), DIALOG_AUDIO);
                fabMenu.collapse();

            }
        });
    }

    private void setFabMenuOpen(boolean open) {

        if (open) {
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
    protected void onRestart() {
        super.onRestart();
        orderCards(chosenItem);
        changeCardView(chosenColumn);
        updateNotes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        orderCards(chosenItem);
        changeCardView(chosenColumn);
    }

    public void updateNotes() {
        notes = DatabaseManager.getInstance(this).getNotes();
        adapter.updateNotes(notes);
    }

    public void addNote(Note note) {
        adapter.addNote(note);
        orderCards(chosenItem);
        empty.setVisibility(View.GONE);
    }

    @Override
    public void onNoteDeleted(int position) {
        adapter.removeNote(position);
        orderCards(chosenItem);
        if (notes.size() == 0) empty.setVisibility(View.VISIBLE);
//        adapter.updateNotes(notes);
    }

    public void generateRandomNotes() {
        Lorem lorem = LoremIpsum.getInstance();
        notes = new ArrayList<>();
        Note temp;
        for (int i = 0; i < 10; i++) {
            temp = new Note(lorem.getWords(1, 4),   //genera da 1 a 4 parole
                    lorem.getParagraphs(1, 3));     //genera da 1 a 3 paragrafi
            notes.add(temp);
        }
        DatabaseManager.getInstance(this).saveNotes(notes);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
//        final ArrayList<Note> filteredNotes= filter(database.getNotes(), query);
//        adapter.animateTo(filteredNotes);
//        recyclerView.scrollToPosition(0);
//        return true;
        return false;
    }

    private ArrayList<Note> filter(ArrayList<Note> notes, String query) {
        query = query.toLowerCase();

        final ArrayList<Note> filteredNote = new ArrayList<>();
        for (Note note : notes) {
            String title = note.getTitle().toLowerCase();
            String text = note.getText().toLowerCase();
            if (title.contains(query) || text.contains(query)) {
                filteredNote.add(note);
            }
        }
        return filteredNote;
    }


    @Override
    public boolean onQueryTextChange(String newText) {
        Log.i(TAG, "onQueryTextChange: " + newText);
        final ArrayList<Note> filteredNotes = filter(DatabaseManager.getInstance(this).getNotes(), newText);
        adapter.animateTo(filteredNotes);
        recyclerView.scrollToPosition(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.order_item) {
            OrderDialog.getInstance(chosenItem).show(getSupportFragmentManager(), DIALOG_SETTINGS);
            return true;
        }

        if (id == R.id.view_item) {
            ChangeViewCardsDialog.getInstance(chosenColumn).show(getSupportFragmentManager(), DIALOG_SETTINGS);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void requestAllPermissions() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_ALL);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ALL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "Grazie capo", Toast.LENGTH_LONG).show();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Toast.makeText(this, "Divertiti, non funzionerà niente", Toast.LENGTH_LONG).show();

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
            }

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(KEY_FABMENU_STATE, fabMenuOpen);
        outState.putInt(KEY_CHOSEN_ITEM, chosenItem);
        outState.putInt(KEY_CHOSEN_COLUMN, chosenColumn);
    }

    @Override
    public void saveMedia(String fileType, File tempFile) {
        //creo la nuova nota e ci associo il file
        Note newNote = new Note();
        newNote.setTitle("Audio note - " + newNote.getDate().toString());

        FileManager.init(this)
                .with(newNote)
                .save(fileType, tempFile);

        try {
            File test = FileManager.init(this)
                    .with(newNote)
                    .get(FileManager.TYPE_AUDIO);
        } catch (Exception e) {
            e.printStackTrace();
        }

        DatabaseManager.getInstance(this).saveNote(newNote);
        addNote(newNote);
    }

    @Override
    public void orderCards(int which) {

        chosenItem = which;

        switch (which) {
            case 0:
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        Collections.sort(notes, new Comparator<Note>() {
                            @Override
                            public int compare(Note lhs, Note rhs) {
                                return lhs.getTitle().toLowerCase().compareTo(rhs.getTitle().toLowerCase());
                            }
                        });
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        adapter.notifyItemRangeChanged(0, adapter.getItemCount());
                    }
                }.execute();
                break;
            case 1:
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        Collections.sort(notes, new Comparator<Note>() {
                            @Override
                            public int compare(Note lhs, Note rhs) {
                                return rhs.getTitle().toLowerCase().compareTo(lhs.getTitle().toLowerCase());
                            }
                        });
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        adapter.notifyItemRangeChanged(0, adapter.getItemCount());
                    }
                }.execute();
                break;
            case 2:
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        Collections.sort(notes, new Comparator<Note>() {
                            @Override
                            public int compare(Note lhs, Note rhs) {
                                return rhs.getDate().compareTo(lhs.getDate());
                            }
                        });
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        adapter.notifyItemRangeChanged(0, adapter.getItemCount());
                    }
                }.execute();
                break;
            case 3:
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        Collections.sort(notes, new Comparator<Note>() {
                            @Override
                            public int compare(Note lhs, Note rhs) {
                                return lhs.getDate().compareTo(rhs.getDate());
                            }
                        });
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        adapter.notifyItemRangeChanged(0, adapter.getItemCount());
                    }
                }.execute();
                break;
        }

    }

    @Override
    public void changeCardView(int column) {
        chosenColumn = column;
        GridLayoutManager layoutManager = new GridLayoutManager(this, column, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}
