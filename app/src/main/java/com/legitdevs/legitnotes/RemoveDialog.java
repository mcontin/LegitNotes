package com.legitdevs.legitnotes;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.legitdevs.legitnotes.database.DatabaseManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class RemoveDialog extends DialogFragment {

    public final static String KEY_NOTE = "note";


    public RemoveDialog() {
        // Required empty public constructor
    }

    public static RemoveDialog getInstance(Note note){
        RemoveDialog removeDialog=new RemoveDialog();
        Bundle bundle=new Bundle();
        bundle.putParcelable(KEY_NOTE, note);
        removeDialog.setArguments(bundle);
        return removeDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());

        builder.setTitle(getResources().getString(R.string.remove_note))
                .setMessage(R.string.remove_dialog_message)
                .setPositiveButton(R.string.remove_dialog_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      DatabaseManager.getInstance(getContext()).removeNote((Note)getArguments().getParcelable(KEY_NOTE));
                        ((HomeActivity)getActivity()).updateNotes();

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
