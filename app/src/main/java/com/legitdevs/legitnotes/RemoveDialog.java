package com.legitdevs.legitnotes;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class RemoveDialog extends DialogFragment {

    public final static String KEY_NOTE = "note";

    private String[] buttons = {"Edit", "Remove"};

    public RemoveDialog() {
        // Required empty public constructor
    }

    public static RemoveDialog getInstance(Note note){
        RemoveDialog removeDialog = new RemoveDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_NOTE, note);
        removeDialog.setArguments(bundle);
        return removeDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setItems(buttons, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which) {
                            //edit
                            case 0:
                                Intent i = new Intent(getContext(), EditNoteActivity.class);
                                i.putExtra(HomeActivity.KEY_NOTE, getArguments().getParcelable(KEY_NOTE));
                                startActivity(i);
                                dismiss();
                                break;
                            //delete
                            case 1:
                                ConfirmRemovalDialog.getInstance((Note) getArguments().getParcelable(KEY_NOTE)).show(getFragmentManager(),"dialog");
                                dismiss();
                                break;
                        }
                    }
                })
        .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });

        // Create the AlertDialog object and return it
        return builder.create();
    }



}
