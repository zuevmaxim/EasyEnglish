package ru.hse.android.easyenglish;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

public class RuleActivity extends DialogFragment {
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        String game = getArguments().getString("game");
        String rule = getArguments().getString("rule");
        return builder
                .setTitle(game + " rules:")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setMessage(rule)
                .setPositiveButton("OK", null)
                .create();
    }
}
