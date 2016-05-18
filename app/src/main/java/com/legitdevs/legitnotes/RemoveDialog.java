package com.legitdevs.legitnotes;


import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.DialogFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class RemoveDialog extends DialogFragment {

    public final static String KEY_NOTE = "note";


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

    String[] array={"Edit","Remove"};

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Builder builder = new Builder(getActivity());
        builder.setTitle("")
                .setItems(array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (array[which] == array[0]) {
                            Intent i = new Intent(getContext(), EditNoteActivity.class);
                            i.putExtra(HomeActivity.KEY_NOTE, getArguments().getParcelable(KEY_NOTE));
                            startActivity(i);
                        } else {
                            ConfirmRemovalDialog.getInstance((Note) getArguments().getParcelable(KEY_NOTE)).show(getFragmentManager(),"dialog");
                        }
                        onDestroy();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }



}
