package com.legitdevs.legitnotes;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConfirmRemovalDialog extends DialogFragment {

    public final static String KEY_NOTE = "note";


    public ConfirmRemovalDialog() {
        // Required empty public constructor
    }

    public static ConfirmRemovalDialog getInstance(Note note){
        ConfirmRemovalDialog confirmRemovalDialog = new ConfirmRemovalDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_NOTE, note);
        confirmRemovalDialog.setArguments(bundle);
        return confirmRemovalDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(getResources().getString(R.string.remove_note))
                .setMessage(R.string.remove_dialog_message)
                .setPositiveButton(R.string.remove_dialog_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
