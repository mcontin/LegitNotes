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
        Document document = database.getExistingDocument(DOCUMENT_NOTES);

        if(document != null) {
            Map<String, Object> notesMap = (HashMap) document.getProperty(PROPERTY_NOTES);

            ArrayList<Note> notes = new ArrayList<>();

            for (String key : notesMap.keySet()) {
                Note note = new Note((HashMap<String, Object>) notesMap.get(key));
                notes.add(note);
            }

            return notes;
        }

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
        //mappa di note
        Map<String, Object> notesMap = new HashMap<>();
        //proprietà del documento a cui verrà aggiunta la mappa di note, quella vecchia verrà sovrascritta
        Map<String, Object> propertiesWithNotes = new HashMap<>();

        Document document = database.getExistingDocument(DOCUMENT_NOTES);

        if(document == null){
            document = database.getDocument(DOCUMENT_NOTES);
        } else {
            propertiesWithNotes.putAll(document.getProperties());
        }

        for(Note note : notes){
            notesMap.put(Integer.toString(note.getId()), note.toHashMap());
        }

        propertiesWithNotes.put(PROPERTY_NOTES, notesMap);

        try {
            //inserisco la lista aggiornata nel documento
            document.putProperties(propertiesWithNotes);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    /**
     * TODO
     * salva la categoria aggiunta dall'utente
     * @param category
     */
    public void addCategory(String category) {

    }

    /**
     * TODO
     * cancella la categoria scelta dall'utente
     */
    public void deleteCategory(String category) {

    }
}
