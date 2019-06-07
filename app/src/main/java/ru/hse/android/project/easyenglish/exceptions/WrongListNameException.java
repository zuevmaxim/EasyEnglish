package ru.hse.android.project.easyenglish.exceptions;

import org.jetbrains.annotations.NotNull;

/**
 * Exception is thrown when list name is illegal in spelling or such list already exists.
 */
public class WrongListNameException extends Exception {
    public WrongListNameException(@NotNull String message) {
        super(message);
    }
}
