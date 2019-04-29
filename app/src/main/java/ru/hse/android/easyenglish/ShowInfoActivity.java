package ru.hse.android.easyenglish;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

public class ShowInfoActivity extends DialogFragment {
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        assert getArguments() != null;
        String title = getArguments().getString("title");
        String message = getArguments().getString("message");
        return builder
                .setTitle(title)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .create();
    }
}
