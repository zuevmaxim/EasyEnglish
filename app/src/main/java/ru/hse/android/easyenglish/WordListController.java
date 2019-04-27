package ru.hse.android.easyenglish;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class WordListController extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "WordLists.db";
    private static final String WORD_LISTS_TABLE_NAME = "word_lists";
    private static final String RANDOM_WORD_LIST_TABLE_NAME = "random_word_list";
    private static final String NAME_COLUMN = "name";
    private static final String CURRENT_LIST_COLUMN = "is_current";
    private static final String ID_COLUMN = "id";
    private static final String WORD_ID_COLUMN = "word_id";

    private static final int RANDOM_WORD_LIST_LENGTH = 10;

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
                        WORD_ID_COLUMN + " INTEGER)");
        db.execSQL(
                "INSERT INTO " + WORD_LISTS_TABLE_NAME +
                "(" + NAME_COLUMN + ", " + CURRENT_LIST_COLUMN + ") VALUES ('" + RANDOM_WORD_LIST_TABLE_NAME + "', 1)");
        // TODO ADD EXTRA WORD LISTS. THIS IS TEMPORARY LIST
        final String TEMPORARY_WORD_LIST_TABLE_NAME = "temporary_word_list";
        db.execSQL(
                "CREATE TABLE " + TEMPORARY_WORD_LIST_TABLE_NAME +
                        "(" + ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        WORD_ID_COLUMN + " INTEGER)");
        db.execSQL(
                "INSERT INTO " + WORD_LISTS_TABLE_NAME +
                        "(" + NAME_COLUMN + ", " + CURRENT_LIST_COLUMN + ") VALUES ('" + TEMPORARY_WORD_LIST_TABLE_NAME + "', 0)");
        db.execSQL(
                "INSERT INTO " + TEMPORARY_WORD_LIST_TABLE_NAME +
                        "(" + WORD_ID_COLUMN + ") VALUES ('" + 42 + "')");
        updateRandomWordList(db);
    }

    private void updateRandomWordList(SQLiteDatabase db) {
        db.execSQL("DELETE FROM " + RANDOM_WORD_LIST_TABLE_NAME);
        WordFactory wordFactory = MainController.getGameController().getWordFactory();
        for (int i = 0; i < RANDOM_WORD_LIST_LENGTH; i++) {
            int wordId = wordFactory.nextWordId();
            db.execSQL(
                    "INSERT INTO " + RANDOM_WORD_LIST_TABLE_NAME +
                            "(" + WORD_ID_COLUMN + ") VALUES ('" + wordId + "')");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public List<String> getWordLists() {
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

    public List<Word> getCurrentListWords() {
        WordFactory wordFactory = MainController.getGameController().getWordFactory();
        List<Word> words = new ArrayList<>();
        String[] columns = {WORD_ID_COLUMN};
        String currentListName = getCurrentWordList().replace(' ', '_');
        Cursor cursor = getReadableDatabase().query(currentListName, columns, null, null, null, null, null);
        while(cursor.moveToNext()) {
            int wordId = cursor.getInt(cursor.getColumnIndexOrThrow(WORD_ID_COLUMN));
            words.add(wordFactory.getWordById(wordId));
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

    public void setCurrentWordList(String newListName) {
        String currentListName = getCurrentWordList().replace(' ', '_');
        String newCurrentListName = newListName.replace(' ', '_');
        getReadableDatabase().execSQL("UPDATE " + WORD_LISTS_TABLE_NAME + " SET " + CURRENT_LIST_COLUMN + " = 0 WHERE " + NAME_COLUMN + " = '" + currentListName + "'");
        getReadableDatabase().execSQL("UPDATE " + WORD_LISTS_TABLE_NAME + " SET " + CURRENT_LIST_COLUMN + " = 1 WHERE " + NAME_COLUMN + " = '" + newCurrentListName + "'");
        MainController.getGameController().getWordStorage().updateStorage();
    }

    public boolean containsWordList(String listName) {
        String name = listName.replace(' ', '_');
        boolean result = false;
        Cursor cursor = getReadableDatabase()
                .query(WORD_LISTS_TABLE_NAME,
                        new String[]{NAME_COLUMN},
                        NAME_COLUMN + " = ? ",
                        new String[]{name}, null, null, null);
        if (cursor.moveToNext()) {
            result = true;
        }
        cursor.close();
        return result;
    }

    public void checkNameSpelling(String name)throws WrongListNameException {
        if (containsWordList(name)) {
            throw new WrongListNameException("Such list already exists.");
        } else if (name.isEmpty()) {
            throw new WrongListNameException("Enter list name.");
        } else if (!name.matches("[A-Za-zА-яа-я][A-Za-zА-яа-я0-9\\s]+")) {
            throw new WrongListNameException("List name should starts with letter and only contains letters and spaces.");
        }
    }

    private void addNewWordList(String name) throws WrongListNameException {
        checkNameSpelling(name);
        String wordListName = name.replace(' ', '_');
        getWritableDatabase().execSQL(
                "CREATE TABLE " + wordListName +
                        "(" + ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        WORD_ID_COLUMN + " INTEGER)");
        getWritableDatabase().execSQL(
                "INSERT INTO " + WORD_LISTS_TABLE_NAME +
                        "(" + NAME_COLUMN + ", " + CURRENT_LIST_COLUMN + ") VALUES ('" + wordListName + "', 0)");
    }

    private void addNewWordIntoList(String name, Word word) throws WrongWordException {
        String wordListName = name.replace(' ', '_');
        WordFactory wordFactory = MainController.getGameController().getWordFactory();
        int wordId = wordFactory.addNewWord(word);
        getWritableDatabase().execSQL(
                "INSERT INTO " + wordListName +
                        "(" + WORD_ID_COLUMN + ") VALUES ('" + wordId + "')");
    }

    public void addNewWordList(String listName, List<Word> wordList) throws WrongListNameException, WrongWordException {
        WordFactory wordFactory = MainController.getGameController().getWordFactory();
        for (Word word : wordList) {
            wordFactory.checkWordSpelling(word);
        }
        addNewWordList(listName);
        for (Word word : wordList) {
            addNewWordIntoList(listName, word);
        }
    }
}
