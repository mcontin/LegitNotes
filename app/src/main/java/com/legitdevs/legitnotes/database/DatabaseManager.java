package com.legitdevs.legitnotes.database;

import android.content.Context;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;
import com.legitdevs.legitnotes.Note;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by mattia on 13/05/16.
 */

public class DatabaseManager {

    public static final String TAG = "DatabaseManager";
    public static final String DB_NAME = "db_notes";
    public static final String DOCUMENT_NOTES = "document_notes";
    public static final String PROPERTY_NOTES = "notes";

    private Manager manager;
    private Database database;
    private Context context;

    public DatabaseManager(Context context) {
        this.context = context;
        createDatabase();
    }

    private void createDatabase(){
        try {
            manager = new Manager(new AndroidContext(context), Manager.DEFAULT_OPTIONS);
            database = manager.getDatabase(DB_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }
    /** TODO
     * Recupera tutte le note dal database
     * @return
     */
    public ArrayList<Note> getNotes() {
        return null;
    }

    /** TODO? si possono filtrare direttamente dopo il getNotes()
     * Recuperare le note di una determinata categoria
     * @param category
     * @return
     */
    public ArrayList<Note> getNotes(String category) {

        return null;
    }

    /**
     * Salva la lista di note nel database
     * @param notes
     */
    public void saveNotes(ArrayList<Note> notes) {
        Document document = database.getDocument(DOCUMENT_NOTES);
        //creando una nuova mappa sovrascrivo completamente le note salvate nel db
        Map<String, Object> notesMap = new HashMap<>();

        for(Note note : notes){
            notesMap.put(Integer.toString(note.getId()), note.toHashMap());
        }

        //con questa mappa creo un campo "notes" nel documento che conterrà la mappa di note
        Map<String, Object> notesToSave = new HashMap<>();
        notesToSave.put(PROPERTY_NOTES, notesMap);

        try {
            //inserisco la lista aggiornata nel documento
            document.putProperties(notesToSave);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

}
