package com.legitdevs.legitnotes;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.legitdevs.legitnotes.database.DatabaseManager;

public class QuickNoteDialog extends DialogFragment {

    private TextView title;
    private TextView text;
    private Context context;

    public QuickNoteDialog() {
        // Empty constructor required for DialogFragment
    }

    public static QuickNoteDialog getInstance(){
        return new QuickNoteDialog();
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {



        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle("Quick Note");
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.quick_note_layout, null);
        title = (TextView) view.findViewById(R.id.quickTitle);
        text = (TextView) view.findViewById(R.id.quickText);

        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.dialog_note_create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Note note = new Note(title.getText().toString(), text.getText().toString());
                        DatabaseManager.getInstance(getContext()).saveNote(note);
                        ((HomeActivity) getActivity()).addNote(note);
                    }
                })
                .setNegativeButton(R.string.dialog_note_discard, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        QuickNoteDialog.this.getDialog().cancel();
                    }
                });

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



