package ru.hse.android.project.easyenglish.controllers.db;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * A database contains word lists.
 * Including special lists: random word list -- contains of random words,
 * and day list -- contains of words with bad statistics and new words,
 * updates every day automatically.
 * A database has a table, where word lists' names are stored,
 * and a table for each list, where words' ids are stored,
 * names of these tables are built in getTableName() method.
 */
public class ListsDB extends SQLiteAssetHelper {

    /** Database version should be updated after each change of application's database. */
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "word_lists.db";
    private static final String WORD_LISTS_TABLE_NAME = "word_lists";
    public static final String RANDOM_WORD_LIST_NAME = "random word list";
    public static final String DAY_LIST_NAME = "day list";
    private static final String NAME_COLUMN = "name";
    private static final String DATE_COLUMN = "date";
    private static final String CURRENT_LIST_COLUMN = "is_current";
    private static final String ID_COLUMN = "id";
    private static final String WORD_ID_COLUMN = "word_id";
    private static final String TABLE = "table";

    /**
     * Constructor loads database if needed
     * and upgrade it if application reinstalled and database version has changed.
     */
    public ListsDB(@NonNull Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade();
    }

    /**
     * Find list's id by name.
     * @param name list name
     * @return list id or 0 if no such list exists
     */
    public int getWordListId(@NonNull  String name) {
        String[] columns = {ID_COLUMN};
        Cursor cursor = getReadableDatabase()
                .query(WORD_LISTS_TABLE_NAME,
                        columns,
                        NAME_COLUMN + " = ?",
                        new String[]{name}, null, null, null);
        int result = 0;
        while (cursor.moveToNext()) {
            result = cursor.getInt(cursor.getColumnIndexOrThrow(ID_COLUMN));
        }
        cursor.close();
        return result;
    }

    /**
     * Construct name of the table, contains a list.
     * The name consists of "table" word and id of the list.
     * @param wordListName list name
     * @return table name
     */
    @NonNull
    private String getTableName(@NonNull String wordListName) {
        return TABLE + getWordListId(wordListName);
    }

    /**
     * Check if random word list is empty.
     * @return true iff random word list is empty
     */
    public boolean isEmptyList(@NonNull String name) {
        String[] columns = {WORD_ID_COLUMN};
        Cursor cursor = getReadableDatabase().query(getTableName(name), columns, null, null, null, null, null);
        if (cursor.moveToNext()) {
            return false;
        }
        cursor.close();
        return true;
    }

    /**
     * Get date of specified list.
     * Used for day list update.
     */
    @NonNull
    public String getListDate(@NonNull String name) {
        Cursor dayListCursor = getReadableDatabase().query(WORD_LISTS_TABLE_NAME,
                new String[] {DATE_COLUMN},
                NAME_COLUMN + " = ?",
                new String[] {name},
                null, null, null);
        String date = "";
        if (dayListCursor.moveToNext()) {
             date = dayListCursor.getString(dayListCursor.getColumnIndexOrThrow(DATE_COLUMN));
        }
        dayListCursor.close();
        return date;
    }

    /**
     * Update random word list by specified ids.
     */
    public void updateRandomWordList(@NonNull List<Integer> ids) {
        String tableName = getTableName(RANDOM_WORD_LIST_NAME);
        getWritableDatabase().execSQL("DELETE FROM " + tableName);
        for (int id : ids) {
            getWritableDatabase().execSQL(
                    "INSERT INTO " + tableName +
                            "(" + WORD_ID_COLUMN + ") VALUES ('" + id + "')");
        }
    }

    /** Set date to a specified list. */
    public void setListDate(@NonNull String list, @NonNull String date) {
        getWritableDatabase().execSQL("UPDATE " + WORD_LISTS_TABLE_NAME + " SET " + DATE_COLUMN + " = " + date +
                " WHERE " + NAME_COLUMN + " = '" + list + "'");
    }

    /**
     * Update day list with specified ids.
     */
    public void updateDayList(@NonNull List<Integer> ids) {
        String tableName = getTableName(DAY_LIST_NAME);
        getWritableDatabase().execSQL("DELETE FROM " + tableName);
        for (int id : ids) {
            getWritableDatabase().execSQL(
                    "INSERT INTO " + tableName +
                            "(" + WORD_ID_COLUMN + ") VALUES ('" + id + "')");
        }
    }

    /** Get a list of names of word lists. */
    @NonNull
    public List<String> getWordLists() {
        List<String> lists = new ArrayList<>();
        String[] columns = {NAME_COLUMN};
        Cursor cursor = getReadableDatabase().query(WORD_LISTS_TABLE_NAME, columns, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String list = cursor.getString(cursor.getColumnIndexOrThrow(NAME_COLUMN));
            lists.add(list);
        }
        cursor.close();
        return lists;
    }

    /** Get words' ids of the specified word list. */
    @NonNull
    public List<Integer> getListWords(@NonNull String wordListName) {
        List<Integer> ids = new ArrayList<>();
        String[] columns = {WORD_ID_COLUMN};
        String tableName = getTableName(wordListName);
        Cursor cursor = getReadableDatabase().query(tableName, columns, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int wordId = cursor.getInt(cursor.getColumnIndexOrThrow(WORD_ID_COLUMN));
            ids.add(wordId);
        }
        cursor.close();
        return ids;
    }

    /** Get name of the current word list. */
    @NonNull
    public String getCurrentWordList() {
        String[] columns = {NAME_COLUMN};
        Cursor cursor = getReadableDatabase()
                .query(WORD_LISTS_TABLE_NAME,
                        columns,
                        CURRENT_LIST_COLUMN + " = ?",
                        new String[]{"1"}, null, null, null);
        String result = "";
        while (cursor.moveToNext()) {
            result = cursor.getString(cursor.getColumnIndexOrThrow(NAME_COLUMN));
        }
        cursor.close();
        return result;
    }

    /** Set new current word list. */
    public void setCurrentValue(@NonNull String listName, @NonNull String value) {
        getWritableDatabase().execSQL("UPDATE " + WORD_LISTS_TABLE_NAME + " SET " + CURRENT_LIST_COLUMN + " = " + value + " WHERE " + NAME_COLUMN + " = '" + listName + "'");
    }

    /** Check if database contains such a list. */
    public boolean containsWordList(@NonNull String listName) {
        boolean result = false;
        Cursor cursor = getReadableDatabase()
                .query(WORD_LISTS_TABLE_NAME,
                        new String[]{NAME_COLUMN},
                        NAME_COLUMN + " = ? ",
                        new String[]{listName}, null, null, null);
        if (cursor.moveToNext()) {
            result = true;
        }
        cursor.close();
        return result;
    }

    /** Add new list into database. */
    private void addNewWordList(@NonNull String name) {
        getWritableDatabase().execSQL(
                "INSERT INTO " + WORD_LISTS_TABLE_NAME +
                        "(" + NAME_COLUMN + ", " + CURRENT_LIST_COLUMN + ") VALUES ('" + name + "', 0)");
        getWritableDatabase().execSQL(
                "CREATE TABLE " + getTableName(name) +
                        "(" + ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        WORD_ID_COLUMN + " INTEGER)");
    }

    /**
     * Add new word into a list. Inserts a word into word database.
     */
    private void addNewWordIntoList(@NonNull String listName, int wordId) {
        getWritableDatabase().execSQL(
                "INSERT INTO " + getTableName(listName) +
                        "(" + WORD_ID_COLUMN + ") VALUES ('" + wordId + "')");
    }

    /**
     * Add new word list into a database with checking the ability of it.
     */
    public void addNewWordList(@NonNull String listName, @NonNull List<Integer> ids) {
        addNewWordList(listName);
        for (int id : ids) {
            addNewWordIntoList(listName, id);
        }
    }

    /**
     * Delete a list from the database.
     * If a list was current then random word list becomes current list.
     */
    public void deleteWordList(@NonNull String name){
        getWritableDatabase().execSQL("DROP TABLE " + getTableName(name));
        getWritableDatabase().execSQL("DELETE FROM " + WORD_LISTS_TABLE_NAME + " WHERE " + ID_COLUMN + " = " + getWordListId(name));
    }
}
