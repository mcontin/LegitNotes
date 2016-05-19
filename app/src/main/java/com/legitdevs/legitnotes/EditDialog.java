package com.legitdevs.legitnotes;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
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

    private String[] buttons = {"Edit", "Remove"};

    private Button btnEdit, btnDelete;

    public EditDialog() {
        // Required empty public constructor
    }

    public static EditDialog getInstance(Note note) {
        EditDialog editDialog = new EditDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_NOTE, note);
        editDialog.setArguments(bundle);
        return editDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v=inflater.inflate(R.layout.dialog_edit_layout, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(v);

        btnEdit=(Button)v.findViewById(R.id.buttonEdit);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), EditNoteActivity.class);
                i.putExtra(HomeActivity.KEY_NOTE, getArguments().getParcelable(KEY_NOTE));
                startActivity(i);
                dismiss();
            }
        });

        btnDelete=(Button)v.findViewById(R.id.buttonDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmRemovalDialog.getInstance(getArguments()).show(getFragmentManager(), "dialog");
                        dismiss();
            }
        });






//        setItems(buttons, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                switch (which) {
//                    //edit
//                    case 0:
//                        Intent i = new Intent(getContext(), EditNoteActivity.class);
//                        i.putExtra(HomeActivity.KEY_NOTE, getArguments().getParcelable(KEY_NOTE));
//                        startActivity(i);
//                        dismiss();
//                        break;
//                    //delete
//                    case 1:
//                        ConfirmRemovalDialog.getInstance((Note) getArguments().getParcelable(KEY_NOTE)).show(getFragmentManager(), "dialog");
//                        dismiss();
//                        break;
//                }
//            }
//        });

        // Create the AlertDialog object and return it
        return builder.create();
    }


}
