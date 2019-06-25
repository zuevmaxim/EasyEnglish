package ru.hse.android.project.easyenglish;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

/** Dialog activity to show messages for player */
public class ShowInfoActivity extends DialogFragment {

    /** Tag to put extra title for hint/rule. */
    public static final String TITLE_TAG = "title";

    /** Tag to put extra message. */
    public static final String MESSAGE_TAG = "message";

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        assert getArguments() != null;
        String title = getArguments().getString(TITLE_TAG);
        String message = getArguments().getString(MESSAGE_TAG);
        return builder
                .setTitle(title)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .create();
    }
}
