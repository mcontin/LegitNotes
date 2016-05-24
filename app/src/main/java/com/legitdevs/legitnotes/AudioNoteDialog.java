package com.legitdevs.legitnotes;


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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.legitdevs.legitnotes.filemanager.FileManager;

import java.io.File;
import java.io.IOException;


public class AudioNoteDialog extends DialogFragment {

    public static final String TAG = "AudioNoteDialog";

    private CircledPulsatingButton btnRecord;

    private boolean recording = false;

    private MediaRecorder mRecorder;

    private ViewGroup awContainer;

    private File mDestFile;
    private Uri mDestFileUri;

    private Button btnSave;

    private IMediaSaver saveHandler;
    private EditText txtAudioNoteTitle;


    public static AudioNoteDialog getInstance() {
        return new AudioNoteDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.audio_note_layout, null);
        builder.setView(v);


        builder.setPositiveButton(R.string.audio_dialog_positive, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                /* User clicked OK so do some stuff */
                Toast.makeText(getContext(), "Note Saved", Toast.LENGTH_SHORT).show();
            }
        })
                .setNegativeButton(R.string.audio_dialog_negative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                /* User clicked cancel so do some stuff */
                        Toast.makeText(getContext(), "Note Dismissed", Toast.LENGTH_SHORT).show();
                    }
                });

        saveHandler = (IMediaSaver) getActivity();

        awContainer = (ViewGroup) v.findViewById(R.id.playerContainer);

        btnSave = (Button) v.findViewById(R.id.saveAudioBtn);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: clicked");

                saveHandler.saveMedia(FileManager.TYPE_AUDIO, mDestFile);

                dismiss();
            }
        });

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

        mDestFile = new File(temporaryDir, "temp.3gp");
        mDestFileUri = Uri.parse(mDestFile.toString());
        txtAudioNoteTitle = (EditText) v.findViewById(R.id.txtAudioNoteTitle);

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

                    //btnRecord.setText("Stop recording");

//                    int dimensions = ((ImageView) v.findViewById(R.id.btnRecord)).getWidth();
//                    int width = (dimensions*3)/2;
//                    int height = (dimensions*3)/2;
//
//                    LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(width,height);
//                    btnRecord.setLayoutParams(parms);

                    btnRecord.setImageResource(R.drawable.ic_stop);
                    recording = true;

                    txtAudioNoteTitle.setVisibility(View.INVISIBLE);

                    btnSave.setEnabled(false);
                    //builder.getButton(AlertDialog.BUTTON1).setEnabled(false);

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
                    //btnRecord.setText("Start recording");

//                    int dimensions = ((ImageView) v.findViewById(R.id.btnRecord)).getWidth();
//                    int width = (dimensions*2)/3;
//                    int height = (dimensions*2)/3;
//                    LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(width,height);
//                    btnRecord.setLayoutParams(parms);

                    btnRecord.setImageResource(R.drawable.ic_keyboard_voice);
                    recording = false;
                    txtAudioNoteTitle.setVisibility(View.VISIBLE);

                    btnSave.setEnabled(true);
                }
            }
        });



//        builder.setTitle("porcodio");
//        builder.setView(R.layout.audio_note_layout);
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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

        AudioWife.getInstance().release();
        awContainer.removeAllViewsInLayout();

        recording = false;



    }

}