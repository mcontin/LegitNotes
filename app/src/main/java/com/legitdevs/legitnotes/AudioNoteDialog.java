package com.legitdevs.legitnotes;


import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import java.io.File;
import java.io.IOException;

import nl.changer.audiowife.AudioWife;

public class AudioNoteDialog extends DialogFragment {

    public static final String TAG = "AudioNoteDialog";

    private Button btnRecord;
    private Button btnPlay;

    private boolean recording = false;
    private boolean playing = false;

    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;

    private ViewGroup awContainer;

    private String mFileName;
    private Uri mFileUri;

    public static AudioNoteDialog getInstance() {
        return new AudioNoteDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.audio_note_layout, container, false);
        getDialog().requestWindowFeature(STYLE_NO_TITLE);

        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";
        mFileUri = Uri.parse(mFileName);

        awContainer = (ViewGroup) view.findViewById(R.id.container);
        // mPlayerContainer = Parent view to add default player UI to.
        AudioWife.getInstance().init(getContext(), mFileUri)
                .useDefaultUi(awContainer, inflater);

        btnRecord = (Button) view.findViewById(R.id.btnRecord);
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!recording) {

                    mRecorder = new MediaRecorder();
                    mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    mRecorder.setOutputFile(mFileName);
                    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

                    try {
                        mRecorder.prepare();
                    } catch (IOException e) {
                        Log.e(TAG, "prepare() failed");
                    }

                    mRecorder.start();

                    btnRecord.setText("Stop recording");
                    recording = true;
                } else {

                    mRecorder.stop();
                    mRecorder.release();
                    mRecorder = null;

                    btnRecord.setText("Start recording");
                    recording = false;
                }
            }
        });

        btnPlay = (Button) view.findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!playing) {

                    mPlayer = new MediaPlayer();
                    try {
                        mPlayer.setDataSource(mFileName);
                        mPlayer.prepare();
                        mPlayer.start();
                    } catch (IOException e) {
                        Log.e(TAG, "prepare() failed");
                    }

                    btnPlay.setText("Stop playing");
                    playing = true;
                } else {
                    mPlayer.release();
                    mPlayer = null;

                    btnPlay.setText("Start playing");
                    playing = false;
                }
            }
        });

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}