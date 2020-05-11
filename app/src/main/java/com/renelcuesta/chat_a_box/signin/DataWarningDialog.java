package com.renelcuesta.chat_a_box.signin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;

public class DataWarningDialog extends DialogFragment {

    public String TAG = "Chat-a-boxDataWarningDialog";

    public interface NoticeDialogListener {

        public void onDialogPositiveClick(DialogFragment dialogFragment);

        public void onDialogNegativeClick(DialogFragment dialogFragment);
    }

    NoticeDialogListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (NoticeDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement " +
                    "NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(
                "We take personal data privacy seriously, it will not be shared with anyone. Please consent to continue.")
                .setPositiveButton("Ok, Accept",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mListener.onDialogPositiveClick(DataWarningDialog.this);
                            }
                        }).setNegativeButton("Decline",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onDialogNegativeClick(DataWarningDialog.this);
                    }
                });

        return builder.create();
    }
}
