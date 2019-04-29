package ru.hse.android.easyenglish;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    private static final int RANDOM_WORD_LIST_LENGTH = 10;

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
        /*
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
        */
        updateRandomWordList(db);
    }

    private void updateRandomWordList(SQLiteDatabase db) {
        String tableName = getTableName(RANDOM_WORD_LIST_TABLE_NAME, db);
        db.execSQL("DELETE FROM " + tableName);
        WordFactory wordFactory = MainController.getGameController().getWordFactory();
        for (int i = 0; i < RANDOM_WORD_LIST_LENGTH; i++) {
            int wordId = wordFactory.nextWordId();
            db.execSQL(
                    "INSERT INTO " + tableName +
                            "(" + WORD_ID_COLUMN + ") VALUES ('" + wordId + "')");
        }
    }

    private void updateRandomWordList() {
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

    private void checkNameSpelling(String name)throws WrongListNameException {
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
                "INSERT INTO " + WORD_LISTS_TABLE_NAME +
                        "(" + NAME_COLUMN + ", " + CURRENT_LIST_COLUMN + ") VALUES ('" + wordListName + "', 0)");
        getWritableDatabase().execSQL(
                "CREATE TABLE " + getTableName(wordListName) +
                        "(" + ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        WORD_ID_COLUMN + " INTEGER)");
    }

    public void addNewWordIntoList(String name, Word word) throws WrongWordException, WrongListNameException {
        String wordListName = name.replace(' ', '_');
        if (!containsWordList(name)) {
            throw new WrongListNameException("No such word list.");
        }
        if (containsWord(wordListName, word)) {
            throw new WrongWordException("Such word already included into list.");
        }
        WordFactory wordFactory = MainController.getGameController().getWordFactory();
        wordFactory.checkWordSpelling(word);
        int wordId = wordFactory.addNewWord(word);
        getWritableDatabase().execSQL(
                "INSERT INTO " + getTableName(wordListName) +
                        "(" + WORD_ID_COLUMN + ") VALUES ('" + wordId + "')");
    }

    public void addNewWordList(String listName, List<Word> wordList) throws WrongListNameException, WrongWordException {
        WordFactory wordFactory = MainController.getGameController().getWordFactory();

        List<Word> listWithoutDuplicates = wordList.stream().distinct().collect(Collectors.toList());
        if (!wordList.equals(listWithoutDuplicates)) {
            throw new WrongWordException("Word duplicate.");
        }

        for (Word word : wordList) {
            wordFactory.checkWordSpelling(word);
        }
        addNewWordList(listName);
        for (Word word : wordList) {
            addNewWordIntoList(listName, word);
        }
    }

    public void setWordListName(String name, String newName) {
        getWritableDatabase().execSQL("UPDATE " + WORD_LISTS_TABLE_NAME +
                " SET " + NAME_COLUMN + " = '" + newName.replace(' ', '_') + "' " +
                "WHERE " + NAME_COLUMN + " = '" + name.replace(' ', '_') + "'");
    }

    public void deleteWordFromList(String name, Word word) throws WrongListNameException {
        String wordListName = name.replace(' ', '_');
        if (!containsWordList(name)) {
            throw new WrongListNameException("No such word list.");
        }
        WordFactory wordFactory = MainController.getGameController().getWordFactory();
        if (!wordFactory.containsWord(word)) {
            return;
        }
        int wordId = 0;
        try {
            wordId = wordFactory.getWordId(word);
        } catch (WrongWordException ignored) { }
        getWritableDatabase().execSQL(
                "DELETE FROM " + getTableName(wordListName) +
                        " WHERE " + WORD_ID_COLUMN + " = " + wordId);
    }

    public void setWordInList(String name, Word word, Word newWord) throws WrongListNameException, WrongWordException {
        String wordListName = name.replace(' ', '_');
        if (!containsWordList(name)) {
            throw new WrongListNameException("No such word list.");
        }
        if (containsWord(wordListName, newWord)) {
            throw new WrongWordException("Such word already included into list.");
        }
        WordFactory wordFactory = MainController.getGameController().getWordFactory();
        int wordId = wordFactory.getWordId(word);
        int newWordId = wordFactory.addNewWord(newWord);
        getWritableDatabase().execSQL(
                "UPDATE " + getTableName(wordListName) +
                        " SET " + WORD_ID_COLUMN + " = " + newWordId +
                        " WHERE " + WORD_ID_COLUMN + " = " + wordId);
    }

    private boolean containsWord(String listName, Word word) throws WrongListNameException {
        if (!containsWordList(listName)) {
            throw new WrongListNameException("No such word list.");
        }
        WordFactory wordFactory = MainController.getGameController().getWordFactory();
        int wordId;
        try {
             wordId = wordFactory.getWordId(word);
        } catch (WrongWordException e) {
            return false;
        }
        String[] columns = {WORD_ID_COLUMN};
        String tableName = getTableName(listName);
        Cursor cursor = getReadableDatabase().query(tableName, columns, WORD_ID_COLUMN + " = ?", new String[]{Integer.toString(wordId)}, null, null, null);
        boolean result = false;
        if (cursor.moveToNext()) {
            result = true;
        }
        cursor.close();
        return result;
    }

    public void deleteWordList(String name) throws WrongListNameException {
        if (!containsWordList(name)) {
            throw new WrongListNameException("No such word list.");
        }
        getWritableDatabase().execSQL("DROP TABLE " + getTableName(name));
        getWritableDatabase().execSQL("DELETE FROM " + WORD_LISTS_TABLE_NAME + " WHERE " + ID_COLUMN + " = " + getWordListId(name));
    }
}
