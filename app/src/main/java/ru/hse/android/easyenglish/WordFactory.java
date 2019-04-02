package ru.hse.android.easyenglish;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class WordFactory extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Words.db";
    private static final String TABLE_NAME = "words";
    private static final String ID_COLUMN = "id";
    private static final String WORD_COLUMN = "word";
    //private static final String DATE_COLUMN = "date";
    private static final String ERRORS_NUMBER_COLUMN = "errors";
    private static final String TOTAL_NUMBER_COLUMN = "total";

    private final Context context;

    public WordFactory(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public String nextWord() {
        String[] columns = {WORD_COLUMN};
        Cursor cursor = getReadableDatabase()
                .query(TABLE_NAME,
                        columns,
                        null, null, null, null, "RANDOM() LIMIT 1");
        String result = "шахматы";
        if (cursor.moveToNext()) {
            result = cursor.getString(cursor.getColumnIndexOrThrow(WORD_COLUMN));
        }
        cursor.close();
        return result;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + TABLE_NAME +
                        "(" +  ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        WORD_COLUMN + " VARCHAR ," +
                        ERRORS_NUMBER_COLUMN + " INTEGER, " +
                        TOTAL_NUMBER_COLUMN + " INTEGER) ");
        try {
            AssetManager assetManager = context.getAssets();
            InputStream is = assetManager.open("words.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            String word;
            while ((word = in.readLine()) != null) {
                if (word.equals("")) {
                    continue;
                }
                db.execSQL("INSERT INTO " + TABLE_NAME + " (" +
                        WORD_COLUMN + ", " +
                        ERRORS_NUMBER_COLUMN + ", " +
                        TOTAL_NUMBER_COLUMN + ") " +
                        "VALUES ('" + TranslateController.translate(word, "en-ru") + "', 0, 0)");
            }
            in.close();
        } catch (IOException e) {
            Log.e("IO exception", e.getMessage());
        }
    }

    public int getWordErrorNumber(String word) {
        int errorNumber = 0;
        String[] columns = {ERRORS_NUMBER_COLUMN};
        Cursor cursor = getReadableDatabase()
                .query(TABLE_NAME,
                        columns,
                        WORD_COLUMN + " = ?", new String[]{word}, null, null, null);

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
                        WORD_COLUMN + " = ?", new String[]{word}, null, null, null);

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
                TOTAL_NUMBER_COLUMN + " = " + previousTotalResult + 1 +
                " WHERE " + WORD_COLUMN + " = '" + word + "'");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
