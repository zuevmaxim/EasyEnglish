package ru.hse.android.project.easyenglish.controllers;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

import ru.hse.android.project.easyenglish.words.Phrase;

/**
 * A database contains phrases list, used in WordPuzzle game.
 * A database has a table, where phrases lists' names are stored,
 * and a table for each list, where phrases are stored.
 */
public class PhrasesController extends SQLiteAssetHelper {

    /** Database version should be updated after each change of application's database. */
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "phrases.db";
    private static final String THEMES_TABLE_NAME = "themes";
    private static final String NAME_COLUMN = "name";
    private static final String CURRENT_LIST_COLUMN = "is_current";
    private static final String RUSSIAN_COLUMN = "Russian";
    private static final String ENGLISH_COLUMN = "English";
    private String currentTheme;

    /**
     * Constructor loads database if needed
     * and upgrade it if application reinstalled and database version has changed.
     */
    PhrasesController(@NonNull Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade();
        updateCurrentTheme();
    }

    /** Get the name of the current phrases list. */
    @NonNull
    public String getCurrentTheme() {
        return currentTheme;
    }

    /** Find new current list. */
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

    /** Set new current theme. And update phrases storage. */
    public void setCurrentTheme(@NonNull String theme) {
        getWritableDatabase().execSQL("UPDATE " + THEMES_TABLE_NAME + " SET " + CURRENT_LIST_COLUMN + " = 0 WHERE " + NAME_COLUMN + " = '" + currentTheme + "'");
        getWritableDatabase().execSQL("UPDATE " + THEMES_TABLE_NAME + " SET " + CURRENT_LIST_COLUMN + " = 1 WHERE " + NAME_COLUMN + " = '" + theme + "'");
        currentTheme = theme;
        MainController.getGameController().getPhraseStorage().updateStorage();
    }

    /** Get a list of phrases from current list. */
    @NonNull
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

    /** Get a list of names of phrases lists. */
    @NonNull
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
