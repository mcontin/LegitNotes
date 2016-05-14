package com.legitdevs.legitnotes.database;

import com.legitdevs.legitnotes.Note;
import java.util.ArrayList;
import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by mattia on 13/05/16.
 */

@Database(name = DatabaseManager.DB_NAME,version = DatabaseManager.VERSION)
public class DatabaseManager {

    public static final String TAG = "DatabaseManager";
    public static final String DB_NAME = "NotesDB";
    public static final String DOCUMENT_NOTES = "DocumentNotes";
    public static final int VERSION=1;


    /** TODO
     * Recupera tutte le note dal database
     * @return
     */
    public static ArrayList<Note> getNotes() {
        return null;
    }

    /** TODO
     * Recuperare le note di una determinata categoria
     * @param category
     * @return
     */
    public static ArrayList<Note> getNotes(String category) {

        return null;
    }

    public static void saveNotes(ArrayList<Note> notes) {

    }

}
