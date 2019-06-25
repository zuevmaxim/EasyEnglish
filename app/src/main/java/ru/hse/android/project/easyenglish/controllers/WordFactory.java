package ru.hse.android.project.easyenglish.controllers;

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
public class WordFactory extends SQLiteAssetHelper {

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
    public WordFactory(@NonNull Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        setForcedUpgrade();
    }

    /** Get next random word. */
    public int nextWordId() {
        return nextWordIds(1).get(0);
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
     * Update day list.
     * @param length length of the list
     * @param newWordsLength preference number of new words
     */
    @NonNull
    public List<Integer> generateDayList(int length, int newWordsLength) {
        String[] columns = {ID_COLUMN};
        Cursor cursor = getReadableDatabase()
                .query(TABLE_NAME,
                        columns,
                        TOTAL_NUMBER_COLUMN + " = 0", null, null, null, "RANDOM() LIMIT " + newWordsLength);
        List<Integer> result = new ArrayList<>();
        while (cursor.moveToNext()) {
            result.add(cursor.getInt(cursor.getColumnIndexOrThrow(ID_COLUMN)));
        }
        cursor.close();
        int rest = length - result.size();
        cursor = getReadableDatabase()
                .query(TABLE_NAME,
                        columns,
                        null, null, null, null, PERCENT_COLUMN + " LIMIT " + rest);
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
    public void resetStatistics(@NonNull Word word) {
        getWritableDatabase().execSQL("UPDATE " + TABLE_NAME + " SET " +
                ERRORS_NUMBER_COLUMN + " = 0, " +
                TOTAL_NUMBER_COLUMN + " = 0, " +
                PERCENT_COLUMN + " = 0" +
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
     * Save result of a game for the word.
     * @param result true iff was given a correct answer
     */
    public void saveWordStatistic(@NonNull Word word, boolean result) {
        int previousErrorResult = getWordErrorNumber(word);
        int previousTotalResult = getWordTotalNumber(word);
        double percent = 1 - (result ? previousErrorResult : previousErrorResult + 1) / (1.0 + previousTotalResult);

        getWritableDatabase().execSQL("UPDATE " + TABLE_NAME + " SET " +
                ERRORS_NUMBER_COLUMN + " = " + (result ? previousErrorResult : previousErrorResult + 1) + ", " +
                TOTAL_NUMBER_COLUMN + " = " + (previousTotalResult + 1) + ", " +
                PERCENT_COLUMN + " = " + percent +
                " WHERE " + RUSSIAN_COLUMN + " = '" + word.getRussian() + "' AND " +
                ENGLISH_COLUMN + " = '" + word.getEnglish() + "'");
    }

    /**
     * Check that word's spelling is legal.
     * @throws WrongWordException if spelling is illegal
     */
    public void checkWordSpelling(@NonNull Word word) throws WrongWordException {
        checkEnglishSpelling(word.getEnglish());
        checkRussianSpelling(word.getRussian());
    }

    /**
     * Check that word's spelling is legal.
     * Word should consists of Russian letters, spaces and some separators.
     * Word should not be empty.
     * @throws WrongWordException if spelling is illegal
     */
    public void checkRussianSpelling(@NonNull String russianWord) throws WrongWordException {
        if (russianWord.isEmpty()) {
            throw new WrongWordException(context.getString(R.string.error_empty_word));
        } else if (!russianWord.matches("[А-Яа-я,.!?\\-\\s]+")) {
            throw new WrongWordException(context.getString(R.string.wrong_word_symbol));
        }
    }

    /**
     * Check that word's spelling is legal.
     * Word should consists of English letters, spaces and some separators.
     * Word should not be empty.
     * @throws WrongWordException if spelling is illegal
     */
    public void checkEnglishSpelling(@NonNull String englishWord) throws WrongWordException {
        if (englishWord.isEmpty()) {
            throw new WrongWordException(context.getString(R.string.error_empty_word));
        }
        if (!englishWord.matches("[A-Za-z,.!?\\-\\s]+")) {
            throw new WrongWordException(context.getString(R.string.wrong_word_symbol));
        }
    }

    /**
     * Add a word into the database.
     * @param word word to add
     * @return word's id in the database
     * @throws WrongWordException if spelling is illegal
     */
    public int addNewWord(@NonNull Word word) throws WrongWordException {
        String russianWord = word.getRussian();
        String englishWord = word.getEnglish();
        checkWordSpelling(word);

        Cursor cursor = getReadableDatabase()
                .query(TABLE_NAME,
                        new String[]{ID_COLUMN},
                        RUSSIAN_COLUMN + " = ? AND " + ENGLISH_COLUMN + " = ?",
                        new String[]{russianWord, englishWord}, null, null, null);
        int id = 1;
        if (cursor.moveToNext()) {
            id = cursor.getInt(cursor.getColumnIndexOrThrow(ID_COLUMN));
            cursor.close();
            if (getWordById(id).getTranscription().isEmpty() && !word.getTranscription().isEmpty()) {
                getWritableDatabase().execSQL("UPDATE " + TABLE_NAME + " SET " + TRANSCRIPTION_COLUMN + " = '" + word.getTranscription() + "' WHERE " + ID_COLUMN + " = " + id);
            }
            return id;
        }
        cursor.close();
        getWritableDatabase().execSQL(
                "INSERT INTO " + TABLE_NAME + "(" + RUSSIAN_COLUMN + "," + ENGLISH_COLUMN + "," + TRANSCRIPTION_COLUMN + ") " +
                        "VALUES ('" + russianWord + "', '" + englishWord + "', '" + word.getTranscription() + "')");
        cursor = getReadableDatabase()
                .query(TABLE_NAME,
                        new String[]{ID_COLUMN},
                        RUSSIAN_COLUMN + " = ? AND " + ENGLISH_COLUMN + " = ?",
                        new String[]{russianWord, englishWord}, null, null, null);
        if (cursor.moveToNext()) {
            id = cursor.getInt(cursor.getColumnIndexOrThrow(ID_COLUMN));
        }
        cursor.close();
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
            throw new WrongWordException("No such word.");
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
