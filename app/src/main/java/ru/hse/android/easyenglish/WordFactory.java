package ru.hse.android.easyenglish;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class WordFactory extends SQLiteAssetHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "dictionary.db";
    private static final String TABLE_NAME = "words";
    private static final String ID_COLUMN = "id";
    private static final String RUSSIAN_COLUMN = "Russian";
    private static final String ENGLISH_COLUMN = "English";
    private static final String TRANSCRIPTION_COLUMN = "transcription";
    //private static final String DATE_COLUMN = "date";
    private static final String ERRORS_NUMBER_COLUMN = "errors";
    private static final String TOTAL_NUMBER_COLUMN = "total";


    public WordFactory(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public String nextWord() {
        String[] columns = {RUSSIAN_COLUMN};
        Cursor cursor = getReadableDatabase()
                .query(TABLE_NAME,
                        columns,
                        null, null, null, null, "RANDOM() LIMIT 1");
        String result = "ошибка"; // TODO
        if (cursor.moveToNext()) {
            result = cursor.getString(cursor.getColumnIndexOrThrow(RUSSIAN_COLUMN));
        }
        cursor.close();
        return result;
    }

    public int getWordErrorNumber(String word) {
        int errorNumber = 0;
        String[] columns = {ERRORS_NUMBER_COLUMN};
        Cursor cursor = getReadableDatabase()
                .query(TABLE_NAME,
                        columns,
                        RUSSIAN_COLUMN + " = ?", new String[]{word}, null, null, null);

        if (cursor.moveToNext()) {
            errorNumber = cursor.getInt(cursor.getColumnIndexOrThrow(ERRORS_NUMBER_COLUMN));
        }
        cursor.close();
        return errorNumber;
    }

    public int getWordTotalNumber(String word) {
        int totalNumber = 0;
        String[] columns = {TOTAL_NUMBER_COLUMN};
        Cursor cursor = getReadableDatabase()
                .query(TABLE_NAME,
                        columns,
                        RUSSIAN_COLUMN + " = ?", new String[]{word}, null, null, null);

        if (cursor.moveToNext()) {
            totalNumber = cursor.getInt(cursor.getColumnIndexOrThrow(TOTAL_NUMBER_COLUMN));
        }
        cursor.close();
        return totalNumber;
    }

    public void saveWordStatistic(String word, boolean result) {
        int previousErrorResult = getWordErrorNumber(word);
        int previousTotalResult = getWordTotalNumber(word);

        getReadableDatabase().execSQL("UPDATE " + TABLE_NAME + " SET " +
                ERRORS_NUMBER_COLUMN + " = " + (result ? previousErrorResult : previousErrorResult + 1) + ", " +
                TOTAL_NUMBER_COLUMN + " = " + (previousTotalResult + 1) +
                " WHERE " + RUSSIAN_COLUMN + " = '" + word + "'");
    }
}
