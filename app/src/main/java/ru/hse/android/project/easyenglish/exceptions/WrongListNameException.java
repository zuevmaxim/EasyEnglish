package ru.hse.android.project.easyenglish.exceptions;

/**
 * Exception is thrown when list name is illegal in spelling or such list already exists.
 */
public class WrongListNameException extends Exception {
    public WrongListNameException(String message) {
        super(message);
    }
}
