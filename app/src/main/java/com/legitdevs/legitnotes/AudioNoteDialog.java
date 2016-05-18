package com.legitdevs.legitnotes;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import java.net.URI;

import nl.changer.audiowife.AudioWife;

public class AudioNoteDialog extends DialogFragment {

    public static final String TAG = "AudioNoteDialog";


    private static final String LOG_TAG = "AudioRecordTest";
    private static final int INTENT_PICK_AUDIO = 1;

    private Context mContext;

    private View mPlayMedia;
    private View mPauseMedia;
    private SeekBar mMediaSeekBar;
    private TextView mRunTime;
    private TextView mTotalTime;
    private TextView mPlaybackTime;
    private static String mFileName = null;





    public static AudioNoteDialog getInstance(){
        return new AudioNoteDialog();
    }




    Button btnRecord,btnReplay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.audio_note_layout, container, false);
        getDialog().requestWindowFeature(STYLE_NO_TITLE);

        View pickAudio = view.findViewById(R.id.pickAudio);

        //inizializza le variabili

        mPlayMedia = view.findViewById(R.id.play);
        mPauseMedia = view.findViewById(R.id.pause);
        mMediaSeekBar = (SeekBar) view.findViewById(R.id.media_seekbar);
        mRunTime = (TextView) view.findViewById(R.id.run_time);
        mTotalTime = (TextView) view.findViewById(R.id.total_time);

        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/tempaudio.3gp";


        //selezione del file da ascoltare

        File externalFile = new File(Environment.getExternalStorageDirectory(),"tempaudio.3gp");

        Uri myURI = Uri.fromFile(externalFile);


        // AudioWife takes care of click handler for play/pause button
        AudioWife.getInstance()
                .init(mContext, myURI)
                .setPlayView(mPlayMedia)
                .setPauseView(mPauseMedia)
                .setSeekBar(mMediaSeekBar)
                .setRuntimeView(mRunTime)
                .setTotalTimeView(mTotalTime);


        // to explicitly pause
        AudioWife.getInstance().pause();


        // when done playing, release the resources
        AudioWife.getInstance().release();

        return view;
    }

















    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


 /*
    @Override
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
*/

}
