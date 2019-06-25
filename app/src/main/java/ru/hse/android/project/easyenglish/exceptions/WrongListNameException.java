package ru.hse.android.project.easyenglish.exceptions;

import android.support.annotation.NonNull;

/**
 * Exception is thrown when list name is illegal in spelling or such list already exists.
 */
public class WrongListNameException extends Exception {
    public WrongListNameException(@NonNull String message) {
        super(message);
    }
}
