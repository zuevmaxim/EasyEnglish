package ru.hse.android.project.easyenglish.exceptions;

/**
 * Exception is thrown if word is illegal in spelling.
 */
public class WrongWordException extends Exception {
    public WrongWordException(String message) {
        super(message);
    }
}
