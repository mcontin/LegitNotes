package com.legitdevs.legitnotes;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;


public class AudioInsideNoteDialog extends DialogFragment {

    public interface IDirAudioNote{
        public void getDirAudio(File dir);
    }

    private IDirAudioNote iDirAudioNote;

    public static final String TAG = "AudioInsideNoteDialog";

    private CircledPulsatingButton btnRecord;

    private boolean recording = false;

    private MediaRecorder mRecorder;

    private ViewGroup awContainer;

    private File mDestFile;
    private Uri mDestFileUri;


    private IMediaSaver saveHandler;

    private AlertDialog dialog;

    public static AudioInsideNoteDialog getInstance() {
        return new AudioInsideNoteDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.audio_inside_note_layout, null);
        builder.setView(v);

        btnRecord = (CircledPulsatingButton) v.findViewById(R.id.btnRecord);
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!recording) {
                    AudioWife.getInstance().release();
                    //bisogna togliere la view altrimenti l'app crasha se si fa partire il player mentre si registra
                    awContainer.removeAllViewsInLayout();

                    mRecorder = new MediaRecorder();
                    mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    mRecorder.setOutputFile(mDestFile.toString());

                    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

                    btnRecord.setMediaRecorder(mRecorder);

                    try {
                        mRecorder.prepare();
                    } catch (IOException e) {
                        Log.e(TAG, "prepare() failed");
                    }

                    mRecorder.start();
                    btnRecord.setImageResource(R.drawable.ic_stop);
                    recording = true;
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

                } else {

                    mRecorder.stop();
                    mRecorder.reset();
                    mRecorder.release();
                    mRecorder = null;
                    btnRecord.releaseRecorder();

                    AudioWife.getInstance().release();
                    awContainer.removeAllViewsInLayout();

                    AudioWife.getInstance()
                            .init(getContext(), mDestFileUri)
                            .useDefaultUi(awContainer, inflater);

                    Log.i(TAG, "onClick: " + mDestFileUri.toString());

                    btnRecord.setImageResource(R.drawable.ic_keyboard_voice_white_24dp);
                    recording = false;

                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);

                }


            }
        });

        saveHandler = (IMediaSaver) getActivity();

        awContainer = (ViewGroup) v.findViewById(R.id.playerContainer);

        //cartella interna privata dell'app
        File internalMemory = getContext().getFilesDir();
        File temporaryDir = new File(internalMemory.getAbsolutePath()
                + "/.temp");

        //se non esiste creo la cartella temporanea
        if (!temporaryDir.exists()) {
            if(!temporaryDir.mkdir()) {
                //è successo qualcosa di brutto
                AlertDialog.Builder b = new AlertDialog.Builder(getContext());
                b.setMessage("Error with internal memory! Please restart the app.");
                b.create().show();
                dismiss();
            }
        }

        mDestFile = new File(temporaryDir + "/audio.3gp");
        mDestFileUri = Uri.parse(mDestFile.toString());



        builder.setPositiveButton(R.string.audio_dialog_positive, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                /* User clicked OK so do some stuff */
                Log.i(TAG, "onClick: clicked");

                //saveHandler.saveMedia(FileManager.TYPE_AUDIO, mDestFile);

                saveDir(mDestFile);

                dismiss();

                //Toast.makeText(getContext(), "Note Saved", Toast.LENGTH_SHORT).show();

            }
        }).setNegativeButton(R.string.audio_dialog_negative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                /* User clicked cancel so do some stuff */
                        Toast.makeText(getContext(), "Note Dismissed", Toast.LENGTH_SHORT).show();
                    }
                });

        dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            }
        });



        return dialog;
    }

    private void saveDir(File dir){
        AudioWife.getInstance().release();

        iDirAudioNote.getDirAudio(dir);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof IDirAudioNote){
            iDirAudioNote= (IDirAudioNote) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        //bisogna spegnere il recorder altrimenti rimane attivo sia quando il dialog viene chiuso sia quando l'app va giù
        //(non testato)
        if(recording){
            mRecorder.stop();
            mRecorder.reset();
            mRecorder.release();
        }
        mRecorder = null;
        btnRecord.releaseRecorder();

        awContainer.removeAllViewsInLayout();

        recording = false;

        iDirAudioNote=null;

    }

}