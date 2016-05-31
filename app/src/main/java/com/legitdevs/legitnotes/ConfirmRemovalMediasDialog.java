package com.legitdevs.legitnotes;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by Michele on 26/05/2016.
 */
public class ConfirmRemovalMediasDialog extends DialogFragment {

    public interface IDeleteMedia {
        void removeMedia(String type);
    }

    private IDeleteMedia iDeleteMedia;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof IDeleteMedia)
            iDeleteMedia = (IDeleteMedia) context;

    }

    @Override
    public void onDetach() {
        super.onDetach();
        iDeleteMedia = null;
    }

    private static final String FILE_KEY = "file";

    public static ConfirmRemovalMediasDialog getInstance(String typeofFile) {
        ConfirmRemovalMediasDialog removalMediasDialog = new ConfirmRemovalMediasDialog();
        Bundle bundle = new Bundle();
        bundle.putString(FILE_KEY, typeofFile);
        removalMediasDialog.setArguments(bundle);
        return removalMediasDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.remove_media_title)
                .setMessage(R.string.remove_media_message)
                .setPositiveButton(R.string.remove_dialog_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteFile(getArguments().getString(FILE_KEY));

                        dismiss();
                    }
                }).setNegativeButton(R.string.remove_dialog_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });

        return builder.create();
    }

    private void deleteFile(String file) {
        iDeleteMedia.removeMedia(file);
    }


}
