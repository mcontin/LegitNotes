package com.legitdevs.legitnotes.database;

import android.content.Context;
import android.util.Log;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;
import com.legitdevs.legitnotes.Category;
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
    public static final String DOCUMENT_CATEGORIES = "document_categories";
    public static final String PROPERTY_CATEGORIES = "categories";

    private Manager manager;
    private Database database;
    private Context context;

    private static DatabaseManager instance;

    public static DatabaseManager getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseManager(context);
        }
        return instance;
    }

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
    /**
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
        } else {
            document = database.getDocument(DOCUMENT_NOTES);
        }

        return new ArrayList<>();
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
            notesMap.put(note.getId().toString(), note.toHashMap());
        }

        propertiesWithNotes.put(PROPERTY_NOTES, notesMap);

        try {
            //inserisco la lista aggiornata nel documento
            document.putProperties(propertiesWithNotes);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    public void saveNote(Note note) {
        //mappa di note
        Map<String, Object> notesMap = new HashMap<>();
        //proprietà del documento a cui verrà aggiunta la mappa di note, quella vecchia verrà sovrascritta
        Map<String, Object> propertiesWithNotes = new HashMap<>();

        Document document = database.getExistingDocument(DOCUMENT_NOTES);

        if(document == null){
            document = database.getDocument(DOCUMENT_NOTES);
        } else {
            propertiesWithNotes.putAll(document.getProperties());
            notesMap.putAll( (HashMap<String, Object>) propertiesWithNotes.get(PROPERTY_NOTES));
        }

        notesMap.put(note.getId().toString(), note.toHashMap());

        propertiesWithNotes.put(PROPERTY_NOTES, notesMap);

        try {
            //inserisco la lista aggiornata nel documento
            document.putProperties(propertiesWithNotes);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    public void removeNote(Note note) {
        //mappa di note
        Map<String, Object> notesMap = new HashMap<>();
        //proprietà del documento a cui verrà aggiunta la mappa di note, quella vecchia verrà sovrascritta
        Map<String, Object> propertiesWithNotes = new HashMap<>();

        Document document = database.getExistingDocument(DOCUMENT_NOTES);

        if(document == null){
            document = database.getDocument(DOCUMENT_NOTES);
        } else {
            propertiesWithNotes.putAll(document.getProperties());
            notesMap.putAll( (HashMap<String, Object>) propertiesWithNotes.get(PROPERTY_NOTES));
        }

        notesMap.remove(note.getId().toString());

        propertiesWithNotes.put(PROPERTY_NOTES, notesMap);

        try {
            //inserisco la lista aggiornata nel documento
            document.putProperties(propertiesWithNotes);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Category> getCategories() {
        Document document = database.getExistingDocument(DOCUMENT_CATEGORIES);

        if(document != null) {
            Map<String, Object> categoriesMap = (HashMap) document.getProperty(PROPERTY_CATEGORIES);

            ArrayList<Category> categories = new ArrayList<>();

            for (String key : categoriesMap.keySet()) {
                Category category = new Category((HashMap<String, Object>) categoriesMap.get(key));
                categories.add(category);
            }

            return categories;
        } else {
            document = database.getDocument(DOCUMENT_NOTES);
        }

        return null;
    }

    /**
     * TODO non so se veramente TODO
     */
    public void saveCategories() {

    }

    /**
     * TODO
     * salva la categoria aggiunta dall'utente
     * @param category
     */
    public void addCategory(Category category) {
        //mappa di categorie
        Map<String, Object> categoriesMap = new HashMap<>();
        //proprietà del documento a cui verrà aggiunta la mappa di categorie, quella vecchia verrà sovrascritta
        Map<String, Object> propertiesWithCategories = new HashMap<>();

        Document document = database.getExistingDocument(DOCUMENT_CATEGORIES);

        if(document == null){
            document = database.getDocument(DOCUMENT_CATEGORIES);
        } else {
            propertiesWithCategories.putAll(document.getProperties());
            categoriesMap.putAll((HashMap<String, Object>) propertiesWithCategories.get(PROPERTY_NOTES));
        }

        categoriesMap.put(category.getName(), category.toHashMap());

        propertiesWithCategories.put(PROPERTY_NOTES, categoriesMap);

        try {
            //inserisco la lista aggiornata nel documento
            document.putProperties(propertiesWithCategories);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    /**
     * TODO
     * cancella la categoria scelta dall'utente
     */
    public void removeCategory(String category) {

    }
}
