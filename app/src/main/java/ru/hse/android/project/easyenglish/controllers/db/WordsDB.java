package ru.hse.android.project.easyenglish.controllers.db;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

import ru.hse.android.project.easyenglish.R;
import ru.hse.android.project.easyenglish.exceptions.WrongWordException;
import ru.hse.android.project.easyenglish.words.Word;

/** A database for holding words and statistics. */
public class WordsDB extends SQLiteAssetHelper {

    /** Database version should be updated after each change of application's database. */
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "dictionary.db";
    private static final String TABLE_NAME = "words";
    private static final String ID_COLUMN = "_id";
    private static final String RUSSIAN_COLUMN = "Russian";
    private static final String ENGLISH_COLUMN = "English";
    private static final String TRANSCRIPTION_COLUMN = "transcription";
    private static final String ERRORS_NUMBER_COLUMN = "errors";
    private static final String TOTAL_NUMBER_COLUMN = "total";
    private static final String PERCENT_COLUMN = "percent";
    private final Context context;

    /**
     * Constructor loads database if needed
     * and upgrade it if application reinstalled and database version has changed.
     */
    public WordsDB(@NonNull Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        setForcedUpgrade();
    }

    /** Get a random set of words of specified length. */
    @NonNull
    public List<Integer> nextWordIds(int length) {
        String[] columns = {ID_COLUMN};
        Cursor cursor = getReadableDatabase()
                .query(TABLE_NAME,
                        columns,
                        null, null, null, null, "RANDOM() LIMIT " + length);
        List<Integer> result = new ArrayList<>();
        while (cursor.moveToNext()) {
            result.add(cursor.getInt(cursor.getColumnIndexOrThrow(ID_COLUMN)));
        }
        cursor.close();
        return result;
    }

    /**
     * Get a list with worst statistics.
     * @param length length of the list
     */
    @NonNull
    public List<Integer> getMinimalList(int length) {
        String[] columns = {ID_COLUMN};
        List<Integer> result = new ArrayList<>();
        Cursor cursor = getReadableDatabase()
                .query(TABLE_NAME,
                        columns,
                        null, null, null, null, PERCENT_COLUMN + " LIMIT " + length);
        while (cursor.moveToNext()) {
            result.add(cursor.getInt(cursor.getColumnIndexOrThrow(ID_COLUMN)));
        }
        cursor.close();
        return result;
    }

    /**
     * Get a list of ids, where words have zero statistics.
     * @param length length of the list
     */
    @NonNull
    public List<Integer> getZeroWords(int length) {
        String[] columns = {ID_COLUMN};
        Cursor cursor = getReadableDatabase()
                .query(TABLE_NAME,
                        columns,
                        TOTAL_NUMBER_COLUMN + " = 0", null, null, null, "RANDOM() LIMIT " + length);
        List<Integer> result = new ArrayList<>();
        while (cursor.moveToNext()) {
            result.add(cursor.getInt(cursor.getColumnIndexOrThrow(ID_COLUMN)));
        }
        cursor.close();
        return result;
    }

    /** Get a word by it's id. */
    @NonNull
    public Word getWordById(int id) {
        String[] columns = {RUSSIAN_COLUMN, ENGLISH_COLUMN, TRANSCRIPTION_COLUMN};
        Cursor cursor = getReadableDatabase()
                .query(TABLE_NAME,
                        columns,
                        ID_COLUMN + " = " + id, null, null, null, null);
        Word word = new Word("ошибка", "error");
        if (cursor.moveToNext()) {
            word.setRussian(cursor.getString(cursor.getColumnIndexOrThrow(RUSSIAN_COLUMN)));
            word.setEnglish(cursor.getString(cursor.getColumnIndexOrThrow(ENGLISH_COLUMN)));
            word.setTranscription(cursor.getString(cursor.getColumnIndexOrThrow(TRANSCRIPTION_COLUMN)));
        }
        cursor.close();
        return word;
    }

    /** Set word's statistics to zero. */
    public void setStatistics(@NonNull Word word, int total, int error, double percent) {
        getWritableDatabase().execSQL("UPDATE " + TABLE_NAME + " SET " +
                ERRORS_NUMBER_COLUMN + " = " + error + " , " +
                TOTAL_NUMBER_COLUMN + " = " + total + ", " +
                PERCENT_COLUMN + " = " + percent +
                " WHERE " + RUSSIAN_COLUMN + " = '" + word.getRussian() + "' AND " +
                ENGLISH_COLUMN + " = '" + word.getEnglish() + "'");
    }

    /** Get number of errors for the word. */
    public int getWordErrorNumber(@NonNull Word word) {
        int errorNumber = 0;
        String[] columns = {ERRORS_NUMBER_COLUMN};
        Cursor cursor = getReadableDatabase()
                .query(TABLE_NAME,
                        columns,
                        RUSSIAN_COLUMN + " = ? AND " + ENGLISH_COLUMN + " = ?",
                        new String[]{word.getRussian(), word.getEnglish()}, null, null, null);

        if (cursor.moveToNext()) {
            errorNumber = cursor.getInt(cursor.getColumnIndexOrThrow(ERRORS_NUMBER_COLUMN));
        }
        cursor.close();
        return errorNumber;
    }

    /** Get totatl number of tests for the word. */
    public int getWordTotalNumber(@NonNull Word word) {
        int totalNumber = 0;
        String[] columns = {TOTAL_NUMBER_COLUMN};
        Cursor cursor = getReadableDatabase()
                .query(TABLE_NAME,
                        columns,
                        RUSSIAN_COLUMN + " = ? AND " + ENGLISH_COLUMN + " = ?",
                        new String[]{word.getRussian(), word.getEnglish()}, null, null, null);

        if (cursor.moveToNext()) {
            totalNumber = cursor.getInt(cursor.getColumnIndexOrThrow(TOTAL_NUMBER_COLUMN));
        }
        cursor.close();
        return totalNumber;
    }

    /**
     * Add a word into the database.
     * @param word word to add
     * @return word's id in the database
     */
    public int addNewWord(@NonNull Word word) {
        getWritableDatabase().execSQL(
                "INSERT INTO " + TABLE_NAME + "(" + RUSSIAN_COLUMN + "," + ENGLISH_COLUMN + "," + TRANSCRIPTION_COLUMN + ") " +
                        "VALUES ('" + word.getRussian() + "', '" + word.getEnglish() + "', '" + word.getTranscription() + "')");

        int id = 0;
        try {
            id = getWordId(word);
        } catch (WrongWordException ignored) { }
        return id;
    }

    /** Get word's id. */
    public int getWordId(@NonNull Word word) throws WrongWordException {
        int wordId;
        String[] columns = {ID_COLUMN};
        Cursor cursor = getReadableDatabase()
                .query(TABLE_NAME,
                        columns,
                        RUSSIAN_COLUMN + " = ? AND " + ENGLISH_COLUMN + " = ?",
                        new String[]{word.getRussian(), word.getEnglish()}, null, null, null);

        if (cursor.moveToNext()) {
            wordId = cursor.getInt(cursor.getColumnIndexOrThrow(ID_COLUMN));
        } else {
            throw new WrongWordException(context.getString(R.string.no_such_word_error));
        }
        cursor.close();
        return wordId;
    }

    /** Check if the database already contains such word. */
    public boolean containsWord(@NonNull Word word) {
        boolean result = false;
        String[] columns = {ID_COLUMN};
        Cursor cursor = getReadableDatabase()
                .query(TABLE_NAME,
                        columns,
                        RUSSIAN_COLUMN + " = ? AND " + ENGLISH_COLUMN + " = ?",
                        new String[]{word.getRussian(), word.getEnglish()}, null, null, null);

        if (cursor.moveToNext()) {
           result = true;
        }
        cursor.close();
        return result;
    }

    /**
     * Get a list of words, which start with specified prefix.
     * It is used in WordChain hints.
     */
    @NonNull
    public List<String> getEnglishWordsStartsWithChar(@NonNull String prefix) {
        List<String> result = new ArrayList<>();
        String[] columns = {ENGLISH_COLUMN};
        Cursor cursor = getReadableDatabase()
                .query(TABLE_NAME,
                        columns,
                        ENGLISH_COLUMN + " LIKE '" + prefix + "%'",
                        null, null, null, "RANDOM()");

        while (cursor.moveToNext()) {
            result.add(cursor.getString(cursor.getColumnIndexOrThrow(ENGLISH_COLUMN)));
        }
        cursor.close();
        return result;
    }

    public void deleteWord(@NonNull Word word) {
        try {
            getWritableDatabase().execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + ID_COLUMN + " = " + getWordId(word));
        } catch (WrongWordException ignore) { }
    }
}
