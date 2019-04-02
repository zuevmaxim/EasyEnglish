package ru.hse.android.easyenglish;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordListController extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "WordLists.db";
    private static final String WORD_LISTS_TABLE_NAME = "word_lists";
    private static final String RANDOM_WORD_LIST_TABLE_NAME = "random_word_list";
    private static final String NAME_COLUMN = "name";
    private static final String CURRENT_LIST_COLUMN = "is_current";
    private static final String ID_COLUMN = "id";
    private static final String WORD_COLUMN = "word";
    public WordListController(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + WORD_LISTS_TABLE_NAME +
                "(" + NAME_COLUMN + " VARCHAR PRIMARY KEY," +
                CURRENT_LIST_COLUMN + " INTEGER)");
        db.execSQL(
                "CREATE TABLE " + RANDOM_WORD_LIST_TABLE_NAME +
                        "(" + ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        WORD_COLUMN + " VARCHAR)");
        db.execSQL(
                "INSERT INTO " + WORD_LISTS_TABLE_NAME +
                "(" + NAME_COLUMN + ", " + CURRENT_LIST_COLUMN + ") VALUES ('" + RANDOM_WORD_LIST_TABLE_NAME + "', 1)");
        updateRandomWordList(db);
    }

    private void updateRandomWordList(SQLiteDatabase db) {
        db.execSQL("DELETE FROM " + RANDOM_WORD_LIST_TABLE_NAME);
        for (int i = 0; i < 10; i++) {
            String word = MainController.getGameController().getWordFactory().nextWord();
            db.execSQL(
                    "INSERT INTO " + RANDOM_WORD_LIST_TABLE_NAME +
                            "(" + WORD_COLUMN + ") VALUES ('" + word + "')");
        }
    }

    public void updateRandomWordList() {
        updateRandomWordList(getWritableDatabase());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private List<String> getWordLists() {
        List<String> lists = new ArrayList<>();
        String[] columns = {NAME_COLUMN};
        Cursor cursor = getReadableDatabase().query(WORD_LISTS_TABLE_NAME, columns, null, null, null, null, null);
        while(cursor.moveToNext()) {
            String list = cursor.getString(cursor.getColumnIndexOrThrow(NAME_COLUMN));
            lists.add(list.replace('_', ' '));
        }
        cursor.close();
        return lists;
    }

    public List<String> getCurrentListWords() {
        List<String> words = new ArrayList<>();
        String[] columns = {WORD_COLUMN};
        String currentListName = getCurrentWordList().replace(' ', '_');
        Cursor cursor = getReadableDatabase().query(currentListName, columns, null, null, null, null, null);
        while(cursor.moveToNext()) {
            String word = cursor.getString(cursor.getColumnIndexOrThrow(WORD_COLUMN));
            words.add(word);
        }
        cursor.close();
        return words;
    }

    public String getCurrentWordList() {
        String[] columns = {NAME_COLUMN};
        Cursor cursor = getReadableDatabase()
                .query(WORD_LISTS_TABLE_NAME,
                        columns,
                        CURRENT_LIST_COLUMN + " = ?",
                        new String[]{"1"}, null, null, null);
        String result = "";
        while(cursor.moveToNext()) {
            String list = cursor.getString(cursor.getColumnIndexOrThrow(NAME_COLUMN));
            result = list.replace('_', ' ');
        }
        cursor.close();
        return result;
    }

    public Map<String, List<String>> getAllWordLists() {
        List<String> lists = getWordLists();
        Map<String, List<String>> result = new HashMap<>();
        for (String list : lists) {
            List<String> wordList = new ArrayList<>();
            String listName = list.replace(' ', '_');
            String[] columns = {WORD_COLUMN};
            Cursor cursor = getReadableDatabase().query(listName, columns, null, null, null, null, null);
            while(cursor.moveToNext()) {
                String word = cursor.getString(cursor.getColumnIndexOrThrow(WORD_COLUMN));
                wordList.add(word);
            }
            cursor.close();
            result.put(list, wordList);
        }
        return result;
    }
}
