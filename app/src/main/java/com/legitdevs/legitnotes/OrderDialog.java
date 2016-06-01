package com.legitdevs.legitnotes;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class OrderDialog extends DialogFragment {

    private static final String INT="int";

    public interface ISelectedItem{
        void orderCards(int which);
    }

    ISelectedItem iSelectedItem;

    public static OrderDialog getInstance(int selected) {
        OrderDialog orderDialog = new OrderDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(INT, selected);
        orderDialog.setArguments(bundle);
        return orderDialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof ISelectedItem)
            iSelectedItem=(ISelectedItem) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        iSelectedItem=null;
    }
    private int select;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        select=getArguments().getInt(INT);

        builder.setTitle(R.string.order_title)
                .setSingleChoiceItems(R.array.order_array, select, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        select=which;
                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        iSelectedItem.orderCards(select);
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

