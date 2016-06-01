package com.legitdevs.legitnotes;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.legitdevs.legitnotes.database.DatabaseManager;
import com.legitdevs.legitnotes.filemanager.FileManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConfirmRemovalDialog extends DialogFragment {

    public final static String KEY_NOTE = "note";
    public final static String KEY_POSITION = "position";

    public ConfirmRemovalDialog() {
        // Required empty public constructor
    }

    public static ConfirmRemovalDialog getInstance(Bundle arguments){
        ConfirmRemovalDialog confirmRemovalDialog = new ConfirmRemovalDialog();
        confirmRemovalDialog.setArguments(arguments);
        return confirmRemovalDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final IDeletionListener listener = (IDeletionListener) getActivity();

        builder.setTitle(getResources().getString(R.string.confirm))
                .setMessage(R.string.remove_dialog_message)
                .setPositiveButton(R.string.remove_dialog_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        DatabaseManager
                                .getInstance(getContext())
                                .removeNote((Note) getArguments().getParcelable(KEY_NOTE));

                        FileManager.init(getContext())
                                .with((Note) getArguments().getParcelable(KEY_NOTE))
                                .deleteMedias();

                        //nota modificata, devo killare l'activity di dettaglio precedente
                        if(NoteDetailActivity.activity != null)
                            NoteDetailActivity.activity.finish();

                        listener.onNoteDeleted(getArguments().getInt(KEY_POSITION));

                        dismiss();
                    }
                })
                .setNegativeButton(R.string.remove_dialog_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });

        return builder.create();
    }
}
