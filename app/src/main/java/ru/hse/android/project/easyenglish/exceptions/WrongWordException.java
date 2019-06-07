package ru.hse.android.project.easyenglish.exceptions;

import org.jetbrains.annotations.NotNull;

/**
 * Exception is thrown if word is illegal in spelling.
 */
public class WrongWordException extends Exception {
    public WrongWordException(@NotNull String message) {
        super(message);
    }
}
