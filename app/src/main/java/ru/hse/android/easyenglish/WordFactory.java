package ru.hse.android.easyenglish;

import android.content.Context;
import android.database.Cursor;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class WordFactory extends SQLiteAssetHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "dictionary.db";
    private static final String TABLE_NAME = "words";
    private static final String ID_COLUMN = "_id";
    private static final String RUSSIAN_COLUMN = "Russian";
    private static final String ENGLISH_COLUMN = "English";
    private static final String TRANSCRIPTION_COLUMN = "transcription";
    //private static final String DATE_COLUMN = "date";
    private static final String ERRORS_NUMBER_COLUMN = "errors";
    private static final String TOTAL_NUMBER_COLUMN = "total";


    public WordFactory(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public int nextWordId() {
        String[] columns = {ID_COLUMN};
        Cursor cursor = getReadableDatabase()
                .query(TABLE_NAME,
                        columns,
                        null, null, null, null, "RANDOM() LIMIT 1");
        int id = 0;
        if (cursor.moveToNext()) {
            id = cursor.getInt(cursor.getColumnIndexOrThrow(ID_COLUMN));
        }
        cursor.close();
        return id;
    }

// --Commented out by Inspection START (25.04.2019 3:19):
//    public Word nextWord() {
//        return getWordById(nextWordId());
//    }
// --Commented out by Inspection STOP (25.04.2019 3:19)

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

    public int getWordErrorNumber(Word word) {
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

    public int getWordTotalNumber(Word word) {
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

    public void saveWordStatistic(Word word, boolean result) {
        int previousErrorResult = getWordErrorNumber(word);
        int previousTotalResult = getWordTotalNumber(word);

        getWritableDatabase().execSQL("UPDATE " + TABLE_NAME + " SET " +
                ERRORS_NUMBER_COLUMN + " = " + (result ? previousErrorResult : previousErrorResult + 1) + ", " +
                TOTAL_NUMBER_COLUMN + " = " + (previousTotalResult + 1) +
                " WHERE " + RUSSIAN_COLUMN + " = '" + word.getRussian() + "' AND " +
                ENGLISH_COLUMN + " = '" + word.getEnglish() + "'");
    }

    public int addNewWord(Word word) throws IllegalArgumentException {
        String russianWord = word.getRussian();
        String englishWord = word.getEnglish();
        if (!russianWord.matches("[А-Яа-я\\s]+") || !englishWord.matches("[A-Za-z\\s]+")) {
            throw new IllegalArgumentException("Words should only contains letters and spaces. Check your spelling.");
        } else {
            Cursor cursor = getReadableDatabase()
                    .query(TABLE_NAME,
                            new String[]{ID_COLUMN},
                            RUSSIAN_COLUMN + " = ? AND " + ENGLISH_COLUMN + " = ?",
                            new String[]{russianWord, englishWord}, null, null, null);
            int id = 1;
            if (cursor.moveToNext()) {
                id = cursor.getInt(cursor.getColumnIndexOrThrow(ID_COLUMN));
                cursor.close();
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
    }
}
