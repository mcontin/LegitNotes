package com.legitdevs.legitnotes.filemanager;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import com.legitdevs.legitnotes.Note;
import com.legitdevs.legitnotes.database.DatabaseManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by mattia on 23/05/16.
 */
public class FileManager {

    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_AUDIO = "audio";
    public static final String TYPE_VIDEO = "video";

    private static Context mContext;
    private static FileManager instance;

    private Note mNote;

    /**
     * inizializza il FileManager con il context per prendere la directory dell'app con getFilesDir()
     * @param context context dell'activity/app
     * @return
     */
    public static FileManager init(Context context) {
        if(mContext == null) mContext = context;
        if(instance == null) instance = new FileManager();
        return instance;
    }

    /**
     * nota su cui si vuole salvare l'allegato
     * @param note
     * @return
     */
    public FileManager with(Note note) {
        mNote = note;
        return this;
    }

    /**
     * Salvataggio di un file
     * @param type  tipo del file, usato per creare la sotto directory e nome del file
     * @param tempFile  file da salvare
     */
    public void save(String type, File tempFile) {
        if(!type.equals(TYPE_IMAGE) && !type.equals(TYPE_AUDIO) && !type.equals(TYPE_VIDEO))
            return;

        //cartella interna privata dell'app
        File internalMemory = mContext.getFilesDir();
        File fileDir = new File(internalMemory.getAbsolutePath()
                + File.separatorChar
                + mNote.getId().toString()
                + File.separatorChar
                + type);
        //nella cartella dell'app ci sarà una cartella per ogni nota che conterrà a sua volta una cartella per ogni tipo di allegato
        //in caso in futuro volessimo salvare più allegati dello stesso tipo

        //se non esiste creo la cartella destinazione
        if (!fileDir.exists()) {
            if(!fileDir.mkdirs()) {
                //è successo qualcosa di brutto
                AlertDialog.Builder b = new AlertDialog.Builder(mContext);
                b.setMessage("Error with internal memory! Please restart the app.");
                b.create().show();
            }
        }

        //filename: audio.3gp, video.mp4, image.jpg
        String finalFilename = type + "." + tempFile.getName().split("\\.")[1];

        //sposto il file da temp a cartella destinazione
        File newFile = new File(fileDir, finalFilename);   //creo un nuovo file che si chiama come l'id della nota per facilitare dopo
        boolean deleted = newFile.delete();
        FileChannel outputChannel = null;
        FileChannel inputChannel = null;
        try {
            outputChannel = new FileOutputStream(newFile).getChannel();
            inputChannel = new FileInputStream(tempFile).getChannel();
            inputChannel.transferTo(0, inputChannel.size(), outputChannel); //trasferisce i dati da input a output: tempFile >> newFile
            inputChannel.close();
            tempFile.delete();
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            if (inputChannel != null) try {
                inputChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (outputChannel != null) try {
                outputChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        mNote.addMedia(type, newFile);

        //saveToDb();
    }

    /**
     * Getter di un file di un certo tipo presente in una nota
     * @param type  tipo file
     * @return
     */
    public File get(String type) throws Exception{
        if(type.equals(TYPE_AUDIO) || type.equals(TYPE_IMAGE) || type.equals(TYPE_VIDEO)) {
            return (mNote.getMedias().get(type) != null) ? new File(mNote.getMedias().get(type)) : new File("");
        }

        throw new Exception("Wrong type, use FileManager.TYPE_*");
    }

    /** TODO
     * Cancella un tipo di file dalla nota selezionata
     * @param type
     */
    public void delete(String type) {
        String removedFile = mNote.getMedias().remove(type);
        if(removedFile != null) {
            File fileToDelete = new File(removedFile);

//            //cartella interna privata dell'app
//            File internalMemory = mContext.getFilesDir();
//            File fileDir = new File(internalMemory.getAbsolutePath()
//                    + File.separatorChar
//                    + mNote.getId().toString()
//                    + File.separatorChar
//                    + type);

            boolean deleted = fileToDelete.delete();
        }
        //saveToDb();
    }

    public void deleteMedias() {
        delete(TYPE_AUDIO);
        delete(TYPE_IMAGE);
        delete(TYPE_VIDEO);
    }

    private void saveToDb() {
        DatabaseManager.getInstance(mContext)
                .saveNote(mNote);
    }

}
