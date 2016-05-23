package com.legitdevs.legitnotes;


import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.PropertyValuesHolder;

import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Executors;


public class AudioNoteDialog extends DialogFragment {

    public static final String TAG = "AudioNoteDialog";

    private CircledPulsatingButton btnRecord;

    private boolean recording = false;

    private MediaRecorder mRecorder;

    private ViewGroup awContainer;

    private String mFileName;
    private Uri mFileUri;

    private Button btnSave;

    private IMediaSaver saveHandler;

    public static AudioNoteDialog getInstance() {
        return new AudioNoteDialog();
    }

//    @Override
//    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        final View view = inflater.inflate(R.layout.audio_note_layout, container, false);
//        getDialog().requestWindowFeature(STYLE_NO_TITLE);
//
//        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
//        mFileName += "/audiorecordtest.3gp";
//        mFileUri = Uri.parse(mFileName);
//
//        awContainer = (ViewGroup) view.findViewById(R.id.playerContainer);
//
//        btnRecord = (Button) view.findViewById(R.id.btnRecord);
//        btnRecord.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!recording) {
//
//                    mRecorder = new MediaRecorder();
//                    mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//                    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//                    mRecorder.setOutputFile(mFileName);
//                    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//
//                    try {
//                        mRecorder.prepare();
//                    } catch (IOException e) {
//                        Log.e(TAG, "prepare() failed");
//                    }
//
//                    mRecorder.start();
//
//                    btnRecord.setText("Stop recording");
//                    recording = true;
//                } else {
//
//                    mRecorder.stop();
//                    mRecorder.release();
//                    mRecorder = null;
//
//                    AudioWife.getInstance().release();
//                    awContainer.removeAllViewsInLayout();
//                    AudioWife.getInstance().init(getContext(), mFileUri)
//                                .useDefaultUi(awContainer, inflater);
//                    btnRecord.setText("Start recording");
//                    recording = false;
//                }
//            }
//        });
//
////        btnPlay = (Button) view.findViewById(R.id.btnPlay);
////        btnPlay.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                if (!playing) {
////
////                    mPlayer = new MediaPlayer();
////                    try {
////                        mPlayer.setDataSource(mFileName);
////                        mPlayer.prepare();
////                        mPlayer.start();
////                    } catch (IOException e) {
////                        Log.e(TAG, "prepare() failed");
////                    }
////
////                    btnPlay.setText("Stop playing");
////                    playing = true;
////                } else {
////                    mPlayer.release();
////                    mPlayer = null;
////
////                    btnPlay.setText("Start playing");
////                    playing = false;
////                }
////            }
////        });
//
//        return view;
//    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.audio_note_layout, null);
        builder.setView(v);

        saveHandler = (IMediaSaver) getActivity();

        awContainer = (ViewGroup) v.findViewById(R.id.playerContainer);

        btnSave = (Button) v.findViewById(R.id.saveAudioBtn);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: clicked");

                saveHandler.saveMedia("audio", mFileName);

                //dopo il salvataggio di una nota audio, il file temporaneo viene cancellato
                //quindi si chiama dismiss per non salvare file vuoti
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

        mFileName = temporaryDir + "/temp.3gp";
        mFileUri = Uri.parse(mFileName);

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
                    mRecorder.setOutputFile(mFileName);

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

                    btnSave.setEnabled(false);
                } else {

                    mRecorder.stop();
                    mRecorder.reset();
                    mRecorder.release();
                    mRecorder = null;
                    btnRecord.releaseRecorder();

                    AudioWife.getInstance().release();
                    awContainer.removeAllViewsInLayout();

                    AudioWife.getInstance().init(getContext(), mFileUri)
                            .useDefaultUi(awContainer, inflater);

                    Log.i(TAG, "onClick: " + mFileUri.toString());
                    //btnRecord.setText("Start recording");

//                    int dimensions = ((ImageView) v.findViewById(R.id.btnRecord)).getWidth();
//                    int width = (dimensions*2)/3;
//                    int height = (dimensions*2)/3;
//                    LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(width,height);
//                    btnRecord.setLayoutParams(parms);

                    btnRecord.setImageResource(R.drawable.ic_keyboard_voice);
                    recording = false;

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