package ru.hse.android.project.easyenglish.controllers;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ru.hse.android.project.easyenglish.exceptions.WrongListNameException;
import ru.hse.android.project.easyenglish.exceptions.WrongWordException;
import ru.hse.android.project.easyenglish.words.Word;
import ru.hse.android.project.easyenglish.words.WordFactory;

public class WordListController extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "WordLists.db";
    private static final String WORD_LISTS_TABLE_NAME = "word_lists";
    private static final String RANDOM_WORD_LIST_TABLE_NAME = "random_word_list";
    private static final String NAME_COLUMN = "name";
    private static final String CURRENT_LIST_COLUMN = "is_current";
    private static final String ID_COLUMN = "id";
    private static final String WORD_ID_COLUMN = "word_id";
    private static final String TABLE = "table";

    private static final int RANDOM_WORD_LIST_LENGTH = 20;

    public WordListController(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private int getWordListId(String name, SQLiteDatabase db) {
        name = name.replace(' ', '_');
        String[] columns = {ID_COLUMN};
        Cursor cursor = db
                .query(WORD_LISTS_TABLE_NAME,
                        columns,
                        NAME_COLUMN + " = ?",
                        new String[]{name}, null, null, null);
        int result = 0;
        while(cursor.moveToNext()) {
            result = cursor.getInt(cursor.getColumnIndexOrThrow(ID_COLUMN));
        }
        cursor.close();
        return result;
    }

    private int getWordListId(String name) {
        return getWordListId(name, getReadableDatabase());
    }

    private String getTableName(String wordListName, SQLiteDatabase db) {
        return TABLE + getWordListId(wordListName, db);
    }

    private String getTableName(String wordListName) {
        return getTableName(wordListName, getReadableDatabase());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + WORD_LISTS_TABLE_NAME +
                "(" + ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NAME_COLUMN + " VARCHAR," +
                CURRENT_LIST_COLUMN + " INTEGER)");
        db.execSQL(
                "INSERT INTO " + WORD_LISTS_TABLE_NAME +
                        "(" + NAME_COLUMN + ", " + CURRENT_LIST_COLUMN + ") VALUES ('" + RANDOM_WORD_LIST_TABLE_NAME + "', 1)");
        db.execSQL(
                "CREATE TABLE " + getTableName(RANDOM_WORD_LIST_TABLE_NAME, db) +
                        "(" + ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        WORD_ID_COLUMN + " INTEGER)");
        updateRandomWordList(db);
    }

    private void updateRandomWordList(SQLiteDatabase db) {
        String tableName = getTableName(RANDOM_WORD_LIST_TABLE_NAME, db);
        db.execSQL("DELETE FROM " + tableName);
        WordFactory wordFactory = MainController.getGameController().getWordFactory();
        for (int i = 0; i < RANDOM_WORD_LIST_LENGTH; i++) {
            int wordId = wordFactory.nextWordId(); //TODO get a list of ids
            db.execSQL(
                    "INSERT INTO " + tableName +
                            "(" + WORD_ID_COLUMN + ") VALUES ('" + wordId + "')");
        }
    }

    public void updateRandomWordList() {
        updateRandomWordList(getWritableDatabase());
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
        return getListWords(getCurrentWordList());
    }

    public List<Word> getListWords(String wordListName) {
        WordFactory wordFactory = MainController.getGameController().getWordFactory();
        List<Word> words = new ArrayList<>();
        String[] columns = {WORD_ID_COLUMN};
        String listName = wordListName.replace(' ', '_');
        String tableName = getTableName(listName);
        Cursor cursor = getReadableDatabase().query(tableName, columns, null, null, null, null, null);
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
        getWritableDatabase().execSQL("UPDATE " + WORD_LISTS_TABLE_NAME + " SET " + CURRENT_LIST_COLUMN + " = 0 WHERE " + NAME_COLUMN + " = '" + currentListName + "'");
        getWritableDatabase().execSQL("UPDATE " + WORD_LISTS_TABLE_NAME + " SET " + CURRENT_LIST_COLUMN + " = 1 WHERE " + NAME_COLUMN + " = '" + newCurrentListName + "'");
        MainController.getGameController().getWordStorage().updateStorage();
    }

    public void setCurrentRandomWordList() {
        setCurrentWordList(RANDOM_WORD_LIST_TABLE_NAME);
    }

    private boolean containsWordList(String listName) {
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

    private void checkListNameSpelling(String name)throws WrongListNameException {
       if (name.isEmpty()) {
            throw new WrongListNameException("Enter list name.");
       } else if (!name.matches("[A-Za-zА-яа-я][A-Za-zА-яа-я0-9\\s]+")) {
            throw new WrongListNameException("List name should starts with letter and only contains letters and spaces.");
       }
    }

    private void addNewWordList(String name) {
        String wordListName = name.replace(' ', '_');
        getWritableDatabase().execSQL(
                "INSERT INTO " + WORD_LISTS_TABLE_NAME +
                        "(" + NAME_COLUMN + ", " + CURRENT_LIST_COLUMN + ") VALUES ('" + wordListName + "', 0)");
        getWritableDatabase().execSQL(
                "CREATE TABLE " + getTableName(wordListName) +
                        "(" + ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        WORD_ID_COLUMN + " INTEGER)");
    }

    private void addNewWordIntoList(String name, Word word) throws WrongWordException {
        String wordListName = name.replace(' ', '_');
        WordFactory wordFactory = MainController.getGameController().getWordFactory();
        int wordId = wordFactory.addNewWord(word);
        getWritableDatabase().execSQL(
                "INSERT INTO " + getTableName(wordListName) +
                        "(" + WORD_ID_COLUMN + ") VALUES ('" + wordId + "')");
    }

    public void addNewWordList(String listName, List<Word> wordList) throws WrongListNameException, WrongWordException {
        if (containsWordList(listName)) {
            throw new WrongListNameException("Such list already exists.");
        }
        checkListNameSpelling(listName);
        checkWordsToAdd(wordList);
        addNewWordList(listName);
        for (Word word : wordList) {
            addNewWordIntoList(listName, word);
        }
    }

    public void deleteWordList(String name) throws WrongListNameException {
        if (!containsWordList(name)) {
            throw new WrongListNameException("No such word list.");
        }
        getWritableDatabase().execSQL("DROP TABLE " + getTableName(name));
        getWritableDatabase().execSQL("DELETE FROM " + WORD_LISTS_TABLE_NAME + " WHERE " + ID_COLUMN + " = " + getWordListId(name));
    }

    public void changeWordList(String name, String newName, List<Word> newWords) throws WrongWordException, WrongListNameException {
        if (!containsWordList(name)) {
            throw new WrongListNameException("No such word list " + name + ".");
        }
        checkListNameSpelling(newName);
        if (!name.equals(newName) && containsWordList(name)) { //same names is OK
            throw new WrongListNameException("Such list already exists.");
        }

        checkWordsToAdd(newWords);

        deleteWordList(name);
        addNewWordList(newName, newWords);
    }

    private void checkWordsToAdd(List<Word> words) throws WrongWordException {
        WordFactory wordFactory = MainController.getGameController().getWordFactory();
        List<Word> listWithoutDuplicates = words.stream().distinct().collect(Collectors.toList());
        if (!words.equals(listWithoutDuplicates)) {
            throw new WrongWordException("Word duplicate.");
        }

        for (Word word : words) {
            wordFactory.checkWordSpelling(word);
        }
    }
}
