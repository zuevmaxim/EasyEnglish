package ru.hse.android.project.easyenglish.controllers;

import android.content.Context;
import android.database.Cursor;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

import ru.hse.android.project.easyenglish.words.Phrase;

public class PhrasesController extends SQLiteAssetHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "phrases.db";
    private static final String THEMES_TABLE_NAME = "themes";
    private static final String NAME_COLUMN = "name";
    private static final String CURRENT_LIST_COLUMN = "is_current";
    private static final String RUSSIAN_COLUMN = "Russian";
    private static final String ENGLISH_COLUMN = "English";
    private String currentTheme;


    public PhrasesController(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade();
        updateCurrentTheme();
    }

    public String getCurrentTheme() {
        return currentTheme;
    }

    private void updateCurrentTheme() {
        String[] columns = {NAME_COLUMN};
        Cursor cursor = getReadableDatabase()
                .query(THEMES_TABLE_NAME,
                        columns,
                        CURRENT_LIST_COLUMN + " = ?",
                        new String[]{"1"}, null, null, null);
        if (cursor.moveToNext()) {
            currentTheme = cursor.getString(cursor.getColumnIndexOrThrow(NAME_COLUMN));
        }
        cursor.close();
    }

    public void setCurrentTheme(String theme) {
        getWritableDatabase().execSQL("UPDATE " + THEMES_TABLE_NAME + " SET " + CURRENT_LIST_COLUMN + " = 0 WHERE " + NAME_COLUMN + " = '" + currentTheme + "'");
        getWritableDatabase().execSQL("UPDATE " + THEMES_TABLE_NAME + " SET " + CURRENT_LIST_COLUMN + " = 1 WHERE " + NAME_COLUMN + " = '" + theme + "'");
        currentTheme = theme;
        MainController.getGameController().getPhraseStorage().updateStorage();
    }

    public List<Phrase> getCurrentThemeList() {
        List<Phrase> result = new ArrayList<>();
        String[] columns = {RUSSIAN_COLUMN, ENGLISH_COLUMN};
        String tableName = currentTheme.replace(' ', '_');
        Cursor cursor = getReadableDatabase()
                .query(tableName, columns, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String russian = cursor.getString(cursor.getColumnIndexOrThrow(RUSSIAN_COLUMN));
            String english = cursor.getString(cursor.getColumnIndexOrThrow(ENGLISH_COLUMN));
            result.add(new Phrase(russian, english));
        }
        cursor.close();
        return result;
    }

    public List<String> getThemesList() {
        List<String> result = new ArrayList<>();
        String[] columns = {NAME_COLUMN};
        Cursor cursor = getReadableDatabase()
                .query(THEMES_TABLE_NAME, columns, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(NAME_COLUMN));
            result.add(name);
        }
        cursor.close();
        return result;
    }
}
