package com.legitdevs.legitnotes;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;

import static android.support.v4.view.GravityCompat.*;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SearchView.OnQueryTextListener,
        IDeletionListener,
        IMediaSaver{

    private static final String DIALOG_QUICK = "quick";
    private static final String DIALOG_CONFIRM = "confirm";
    private static final String DIALOG_AUDIO = "audio";
    private static final String TAG = "HomeActivity";

    private static final int REQUEST_PERMISSION_LOCATION = 1;
    private static final int REQUEST_PERMISSION_WRITE_STORAGE = 2;
    private static final int REQUEST_PERMISSION_MICROPHONE = 3;
    private static final int REQUEST_ALL = 4;

    public static final String KEY_NOTES_LIST = "notes_list";
    public static final String KEY_NOTE = "note";
    public static final String KEY_SEARCH ="search";

    private RecyclerView recyclerView;
    private NotesAdapter adapter;
    private ArrayList<Note> notes;
    private FloatingActionButton FABQuickNote, FABNewNote, FABNewAudioNote, FABVideo, FABLocation;

    private SearchView searchView;
    private MenuItem searchMenuItem;

    public static HomeActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;

        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
        ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestAllPermissions();
        }

        if(savedInstanceState != null) {
            notes = savedInstanceState.getParcelableArrayList(KEY_NOTES_LIST);
        } else {
            notes = DatabaseManager.getInstance(this).getNotes();

            if (notes.size() == 0) {
                generateRandomNotes();
            }
        }

        //FAB creazione note
        final FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        assert frameLayout != null;
        frameLayout.getBackground().setAlpha(0);
        final FloatingActionsMenu fabMenu = (FloatingActionsMenu) findViewById(R.id.fab_menu);
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

                        Intent i = new Intent(getBaseContext(),EditNoteActivity.class);
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

            @Override
            public void onMenuCollapsed() {
                frameLayout.getBackground().setAlpha(0);
                frameLayout.setOnTouchListener(null);
            }
        });

        //lista note
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new NotesAdapter(notes, this);    //adapter per la lista di note e creazione delle Card
        //layout a 2 colonne
        GridLayoutManager layoutManager = new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);


        //DRAWER LATERALE
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    @Override
    protected void onRestart() {
        super.onRestart();

        updateNotes();
    }

    public void updateNotes() {
        notes = DatabaseManager.getInstance(this).getNotes();
        adapter.updateNotes(notes);
    }
    public void addNote(Note note) {
        adapter.addNote(note);
    }

    @Override
    public void onNoteDeleted(int position) {
        adapter.removeNote(position);
        adapter.updateNotes(notes);
    }

    public void generateRandomNotes(){
        Lorem lorem = LoremIpsum.getInstance();
        notes = new ArrayList<>();
        Note temp;
        for(int i = 0; i < 10; i++) {
            temp = new Note(lorem.getWords(1, 4),   //genera da 1 a 4 parole
                    lorem.getParagraphs(1, 3));     //genera da 1 a 3 paragrafi
            notes.add(temp);
        }
        DatabaseManager.getInstance(this).saveNotes(notes);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(START)) {
            drawer.closeDrawer(START);
        } else {
            super.onBackPressed();
        }
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
            final String title = note.getTitle().toLowerCase();
            if (title.contains(query)) {
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

        //TODO opzioni ordinamento
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(START);
        return true;
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
        outState.putParcelableArrayList(KEY_NOTES_LIST, notes);
    }

    @Override
    public void saveMedia(String fileType, File tempFile) {
        //creo la nuova nota e ci associo il file
        Note newNote = new Note();
        newNote.setTitle("Audio note - " + newNote.getDate().toString());

        FileManager.init(this)
                .with(newNote)
                .save(fileType, tempFile);

        File test = FileManager.init(this)
                .with(newNote)
                .get(FileManager.TYPE_AUDIO);

        DatabaseManager.getInstance(this).addNote(newNote);
        addNote(newNote);
    }
}
