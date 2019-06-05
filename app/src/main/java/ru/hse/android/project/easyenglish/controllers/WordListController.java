package ru.hse.android.project.easyenglish.controllers;


import android.content.Context;
import android.database.Cursor;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ru.hse.android.project.easyenglish.exceptions.WrongListNameException;
import ru.hse.android.project.easyenglish.exceptions.WrongWordException;
import ru.hse.android.project.easyenglish.words.Word;
import ru.hse.android.project.easyenglish.words.WordFactory;

public class WordListController extends SQLiteAssetHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "word_lists.db";
    private static final String WORD_LISTS_TABLE_NAME = "word_lists";
    public static final String RANDOM_WORD_LIST_NAME = "random word list";
    private static final String DAY_LIST_NAME = "day list";
    private static final String NAME_COLUMN = "name";
    private static final String DATE_COLUMN = "date";
    private static final String CURRENT_LIST_COLUMN = "is_current";
    private static final String ID_COLUMN = "id";
    private static final String WORD_ID_COLUMN = "word_id";
    private static final String TABLE = "table";

    private static final int RANDOM_WORD_LIST_LENGTH = 20;
    private static final int DAY_LIST_LENGTH = 10;
    private static final int PREF_NEW_WORDS = 7;
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

    public WordListController(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public int getWordListId(String name) {
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

    public int getRandomWordListId() {
        return getWordListId(RANDOM_WORD_LIST_NAME);
    }

    public int getDayListId() {
        return getWordListId(DAY_LIST_NAME);
    }

    private String getTableName(String wordListName) {
        return TABLE + getWordListId(wordListName);
    }

    public boolean needsInit() {
        String[] columns = {WORD_ID_COLUMN};
        Cursor cursor = getReadableDatabase().query(getTableName(RANDOM_WORD_LIST_NAME), columns, null, null, null, null, null);
        if (cursor.moveToNext()) {
            return false;
        }
        cursor.close();
        return true;
    }

    public boolean needsDayListInit() {
        Cursor dayListCursor = getReadableDatabase().query(WORD_LISTS_TABLE_NAME,
                new String[] {DATE_COLUMN},
                NAME_COLUMN + " = ?",
                new String[] {DAY_LIST_NAME},
                null, null, null);
        boolean result = false;
        String currentDate = format.format(System.currentTimeMillis());
        if (dayListCursor.moveToNext()) {
            String date = dayListCursor.getString(dayListCursor.getColumnIndexOrThrow(DATE_COLUMN));
            if (!date.equals(currentDate)) {
                result = true;
            }
        } else {
            result = true;
        }
        dayListCursor.close();
        return result;
    }

    public void updateRandomWordList() {
        String tableName = getTableName(RANDOM_WORD_LIST_NAME);
        getWritableDatabase().execSQL("DELETE FROM " + tableName);
        WordFactory wordFactory = MainController.getGameController().getWordFactory();
        List<Integer> ids = wordFactory.nextWordIds(RANDOM_WORD_LIST_LENGTH);
        for (int id : ids) {
            getWritableDatabase().execSQL(
                    "INSERT INTO " + tableName +
                            "(" + WORD_ID_COLUMN + ") VALUES ('" + id + "')");
        }
        MainController.getGameController().getWordStorage().updateStorage();
    }

    public void updateDayList() {
        String currentDate = format.format(System.currentTimeMillis());
        getWritableDatabase().execSQL("UPDATE " + WORD_LISTS_TABLE_NAME + " SET " + DATE_COLUMN + " = " + currentDate +
                " WHERE " + NAME_COLUMN + " = '" + DAY_LIST_NAME + "'");
        String tableName = getTableName(DAY_LIST_NAME);
        getWritableDatabase().execSQL("DELETE FROM " + tableName);
        WordFactory wordFactory = MainController.getGameController().getWordFactory();
        List<Integer> ids = wordFactory.generateDayList(DAY_LIST_LENGTH, PREF_NEW_WORDS);
        for (int id : ids) {
            getWritableDatabase().execSQL(
                    "INSERT INTO " + tableName +
                            "(" + WORD_ID_COLUMN + ") VALUES ('" + id + "')");
        }
        MainController.getGameController().getWordStorage().updateStorage();
    }

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

    public List<Word> getCurrentListWords() {
        return getListWords(getCurrentWordList());
    }

    public List<Word> getListWords(String wordListName) {
        WordFactory wordFactory = MainController.getGameController().getWordFactory();
        List<Word> words = new ArrayList<>();
        String[] columns = {WORD_ID_COLUMN};
        String tableName = getTableName(wordListName);
        Cursor cursor = getReadableDatabase().query(tableName, columns, null, null, null, null, null);
        while (cursor.moveToNext()) {
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
        while (cursor.moveToNext()) {
            result = cursor.getString(cursor.getColumnIndexOrThrow(NAME_COLUMN));
        }
        cursor.close();
        return result;
    }

    public void setCurrentWordList(String newListName) {
        String currentListName = getCurrentWordList();
        getWritableDatabase().execSQL("UPDATE " + WORD_LISTS_TABLE_NAME + " SET " + CURRENT_LIST_COLUMN + " = 0 WHERE " + NAME_COLUMN + " = '" + currentListName + "'");
        getWritableDatabase().execSQL("UPDATE " + WORD_LISTS_TABLE_NAME + " SET " + CURRENT_LIST_COLUMN + " = 1 WHERE " + NAME_COLUMN + " = '" + newListName + "'");
        MainController.getGameController().getWordStorage().updateStorage();
    }

    public void setCurrentDayList() {
        setCurrentWordList(DAY_LIST_NAME);
    }

    private boolean containsWordList(String listName) {
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

    private void checkListNameSpelling(String name)throws WrongListNameException {
       if (name.isEmpty()) {
            throw new WrongListNameException("Enter list name.");
       } else if (!name.matches("[A-Za-zА-яа-я][A-Za-zА-яа-я0-9\\s]+")) {
            throw new WrongListNameException("List name should starts with letter and only contains letters and spaces.");
       }
    }

    private void addNewWordList(String name) {
        getWritableDatabase().execSQL(
                "INSERT INTO " + WORD_LISTS_TABLE_NAME +
                        "(" + NAME_COLUMN + ", " + CURRENT_LIST_COLUMN + ") VALUES ('" + name + "', 0)");
        getWritableDatabase().execSQL(
                "CREATE TABLE " + getTableName(name) +
                        "(" + ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        WORD_ID_COLUMN + " INTEGER)");
    }

    private void addNewWordIntoList(String name, Word word) throws WrongWordException {
        WordFactory wordFactory = MainController.getGameController().getWordFactory();
        int wordId = wordFactory.addNewWord(word);
        getWritableDatabase().execSQL(
                "INSERT INTO " + getTableName(name) +
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
        if (getWordListId(name) == getWordListId(getCurrentWordList())) {
            setCurrentWordList(RANDOM_WORD_LIST_NAME);
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
        boolean wasCurrent = getWordListId(name) == getWordListId(getCurrentWordList());
        deleteWordList(name);
        addNewWordList(newName, newWords);
        if (wasCurrent) {
            setCurrentWordList(newName);
        }
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
