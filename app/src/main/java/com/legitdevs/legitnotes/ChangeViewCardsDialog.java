package com.legitdevs.legitnotes;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class ChangeViewCardsDialog extends DialogFragment {

    public interface ISelectedItem{
        void changeCardView(int column);
    }

    ISelectedItem iSelectedItem;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if(activity instanceof ISelectedItem )
            iSelectedItem = (ISelectedItem) activity;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        iSelectedItem = null;
    }

    private static final String COLUMN="int column";

    public static ChangeViewCardsDialog getInstance(int columns){
        ChangeViewCardsDialog changeViewCardsDialog = new ChangeViewCardsDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(COLUMN, columns);
        changeViewCardsDialog.setArguments(bundle);
        return changeViewCardsDialog;
    }

    private int column;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        column=getArguments().getInt(COLUMN);

        builder.setTitle(R.string.card_view_title)
                .setSingleChoiceItems(R.array.card_view_array , column-1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        column=which+1;
                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        iSelectedItem.changeCardView(column);
                        dismiss();
                    }
                })
                .setNegativeButton(R.string.no_new_setting, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });



        return builder.create();
    }
}
