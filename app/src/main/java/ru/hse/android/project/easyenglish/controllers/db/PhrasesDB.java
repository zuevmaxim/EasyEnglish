package ru.hse.android.project.easyenglish.controllers.db;

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
public class PhrasesDB extends SQLiteAssetHelper {

    /** Database version should be updated after each change of application's database. */
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "phrases.db";
    private static final String THEMES_TABLE_NAME = "themes";
    private static final String NAME_COLUMN = "name";
    private static final String CURRENT_LIST_COLUMN = "is_current";
    private static final String RUSSIAN_COLUMN = "Russian";
    private static final String ENGLISH_COLUMN = "English";

    /**
     * Constructor loads database if needed
     * and upgrade it if application reinstalled and database version has changed.
     */
    public PhrasesDB(@NonNull Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade();
    }

    /** Find list with specified current value. */
    public String getThemeWithCurrentValue(@NonNull String current) {
        String[] columns = {NAME_COLUMN};
        Cursor cursor = getReadableDatabase()
                .query(THEMES_TABLE_NAME,
                        columns,
                        CURRENT_LIST_COLUMN + " = ?",
                        new String[]{current}, null, null, null);
        String currentTheme = null;
        if (cursor.moveToNext()) {
            currentTheme = cursor.getString(cursor.getColumnIndexOrThrow(NAME_COLUMN));
        }
        cursor.close();
        return currentTheme;
    }

    /** Set current value for a theme. */
    public void setThemeCurrentValue(@NonNull String theme, @NonNull String value) {
        getWritableDatabase().execSQL("UPDATE " + THEMES_TABLE_NAME + " SET " + CURRENT_LIST_COLUMN + " = " + value + " WHERE " + NAME_COLUMN + " = '" + theme + "'");
    }

    /** Get a list of phrases from theme. */
    @NonNull
    public List<Phrase> getThemeList(@NonNull String theme) {
        List<Phrase> result = new ArrayList<>();
        String[] columns = {RUSSIAN_COLUMN, ENGLISH_COLUMN};
        String tableName = theme.replace(' ', '_');
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
