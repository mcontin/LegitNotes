package com.legitdevs.legitnotes;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditDialog extends DialogFragment {

    public final static String KEY_NOTE = "note";

    public final static String KEY_POSITION = "position";

    private String[] buttons = {"Edit", "Remove"};

    private Button btnEdit, btnDelete;

    public EditDialog() {
        // Required empty public constructor
    }

    public static EditDialog getInstance(Note note, int position) {
        EditDialog editDialog = new EditDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_NOTE, note);
        bundle.putInt(KEY_POSITION, position);
        editDialog.setArguments(bundle);
        return editDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_edit_layout, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(v);

        btnEdit = (Button) v.findViewById(R.id.buttonEdit);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), EditNoteActivity.class);
                i.putExtra(KEY_NOTE, getArguments().getParcelable(KEY_NOTE));
                startActivity(i);
                dismiss();
            }
        });

        btnDelete = (Button) v.findViewById(R.id.buttonDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Note note = getArguments().getParcelable(KEY_NOTE);
                int position = getArguments().getInt(KEY_POSITION);
                Bundle bundle = new Bundle();
                bundle.putParcelable(KEY_NOTE, note);
                bundle.putInt(KEY_POSITION, position);
                ConfirmRemovalDialog.getInstance(bundle).show(getFragmentManager(), "dialog");
                dismiss();
            }
        });


        return builder.create();
    }


}
