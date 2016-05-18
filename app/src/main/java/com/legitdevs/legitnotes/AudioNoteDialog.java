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

    private static final String LOG_TAG = "AudioRecordTest";

    private Context mContext;

    private View mPlayMedia;
    private View mPauseMedia;
    private SeekBar mMediaSeekBar;
    private TextView mRunTime;
    private TextView mTotalTime;
//    private TextView mPlaybackTime;
    private static String mFileName;
    private boolean recording = false;
    private MediaRecorder myAudioRecorder;
    private Button btnRecord;
    private File externalFile;
    private Uri myURI;
    private Button playAudio;

    public static AudioNoteDialog getInstance() {
        return new AudioNoteDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.audio_note_layout, container, false);
        getDialog().requestWindowFeature(STYLE_NO_TITLE);

        //View pickAudio = view.findViewById(R.id.pickAudio);

        //selezione del file da ascoltare
        externalFile = new File(mFileName);
        myURI = Uri.fromFile(externalFile);

        mContext = getActivity().getApplicationContext();

        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tempaudio.3gp";

        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(mFileName);

        //inizializza le variabili

        mPlayMedia = view.findViewById(R.id.play);
        mPauseMedia = view.findViewById(R.id.pause);
        mMediaSeekBar = (SeekBar) view.findViewById(R.id.media_seekbar);
        mRunTime = (TextView) view.findViewById(R.id.run_time);
        mTotalTime = (TextView) view.findViewById(R.id.total_time);

        playAudio = (Button) view.findViewById(R.id.btnPlay);
        playAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlaying();
            }
        });

        btnRecord = (Button) view.findViewById(R.id.btnRecord);
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recording){

                    myAudioRecorder.stop();
                    myAudioRecorder.release();
                    recording = false;

                    btnRecord.setText("Start Recording");

                } else {
                    recording = true;
                    try {
                        myAudioRecorder.prepare();
                        myAudioRecorder.start();
                    }

                    catch (IllegalStateException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        recording = false;
                    }

                    catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        recording = false;
                    }

                    // AudioWife takes care of click handler for play/pause button
                    AudioWife.getInstance()
                            .init(mContext, myURI)
                            .setPlayView(mPlayMedia)
                            .setPauseView(mPauseMedia)
                            .setSeekBar(mMediaSeekBar)
                            .setRuntimeView(mRunTime)
                            .setTotalTimeView(mTotalTime);

                    /*
                    // to explicitly pause
                    AudioWife.getInstance().pause();

                    // when done playing, release the resources
                    AudioWife.getInstance().release();
                    */

                    btnRecord.setText("Stop Recording");
                }

//                Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
            }
        });
        //inutile btw
        //mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        //mFileName += "/tempaudio.3gp";

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void startPlaying() {
        MediaPlayer mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }
}
