package ru.hse.android.project.easyenglish.exceptions;

import android.support.annotation.NonNull;

/**
 * Exception is thrown if word is illegal in spelling.
 */
public class WrongWordException extends Exception {
    public WrongWordException(@NonNull String message) {
        super(message);
    }
}
