package com.legitdevs.legitnotes.database;

import com.legitdevs.legitnotes.Note;
import java.util.ArrayList;

/**
 * Created by mattia on 13/05/16.
 */
public class DatabaseManager {

    public static final String TAG = "DatabaseManager";
    public static final String DB_NAME = "NotesDB";
    public static final String DOCUMENT_NOTES = "DocumentNotes";


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
