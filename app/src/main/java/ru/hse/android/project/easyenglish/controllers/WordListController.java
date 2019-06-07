package ru.hse.android.project.easyenglish.controllers;


import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ru.hse.android.project.easyenglish.exceptions.WrongListNameException;
import ru.hse.android.project.easyenglish.exceptions.WrongWordException;
import ru.hse.android.project.easyenglish.words.Word;
import ru.hse.android.project.easyenglish.words.WordFactory;

/**
 * A database contains word lists.
 * Including special lists: random word list -- contains of random words,
 * and day list -- contains of words with bad statistics and new words,
 * updates every day automatically.
 * A database has a table, where word lists' names are stored,
 * and a table for each list, where words' ids are stored,
 * names of these tables are built in getTableName() method.
 */
public class WordListController extends SQLiteAssetHelper {
    /** Database version should be updated after each change of application's database. */
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "word_lists.db";
    private static final String WORD_LISTS_TABLE_NAME = "word_lists";
    private static final String RANDOM_WORD_LIST_NAME = "random word list";
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
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

    /**
     * Constructor loads database if needed
     * and upgrade it if application reinstalled and database version has changed.
     */
    WordListController(@NotNull Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade();
    }

    /**
     * Find list's id by name.
     * @param name list name
     * @return list id or 0 if no such list exists
     */
    public int getWordListId(@NotNull String name) {
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

    /** Get id of the random word list. */
    public int getRandomWordListId() {
        return getWordListId(RANDOM_WORD_LIST_NAME);
    }

    /** Get id of the day list. */
    public int getDayListId() {
        return getWordListId(DAY_LIST_NAME);
    }

    /**
     * Construct name of the table, contains a list.
     * The name consists of "table" word and id of the list.
     * @param wordListName list name
     * @return table name
     */
    @NotNull
    private String getTableName(@NotNull String wordListName) {
        return TABLE + getWordListId(wordListName);
    }

    /**
     * Check if random word list is empty.
     * @return true iff random word list is empty
     */
    boolean needsInit() {
        String[] columns = {WORD_ID_COLUMN};
        Cursor cursor = getReadableDatabase().query(getTableName(RANDOM_WORD_LIST_NAME), columns, null, null, null, null, null);
        if (cursor.moveToNext()) {
            return false;
        }
        cursor.close();
        return true;
    }

    /**
     * Check if day list needs init.
     * @return true iff day list is outdated
     */
    boolean needsDayListInit() {
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

    /**
     * Update random word list.
     * Generate new random set of words.
     * And update word storage.
     */
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

    /**
     * Update day list, and update word storage.
     * Generates a new word list, where some words are new and the others are with bad statistics.
     */
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

    /** Get a list of names of word lists. */
    @NotNull
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

    /** Get words of the current word list. */
    @NotNull
    public List<Word> getCurrentListWords() {
        return getListWords(getCurrentWordList());
    }

    /** Get words of the specified word list. */
    @NotNull
    public List<Word> getListWords(@NotNull String wordListName) {
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

    /** Get name of the current word list. */
    @NotNull
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
    public void setCurrentWordList(@NotNull String newListName) {
        String currentListName = getCurrentWordList();
        getWritableDatabase().execSQL("UPDATE " + WORD_LISTS_TABLE_NAME + " SET " + CURRENT_LIST_COLUMN + " = 0 WHERE " + NAME_COLUMN + " = '" + currentListName + "'");
        getWritableDatabase().execSQL("UPDATE " + WORD_LISTS_TABLE_NAME + " SET " + CURRENT_LIST_COLUMN + " = 1 WHERE " + NAME_COLUMN + " = '" + newListName + "'");
        MainController.getGameController().getWordStorage().updateStorage();
    }

    /** Set day list as current. */
    public void setCurrentDayList() {
        setCurrentWordList(DAY_LIST_NAME);
    }

    /** Check if database contains such a list. */
    private boolean containsWordList(@NotNull String listName) {
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

    /**
     * Check list name. Name of a list should be non-empty
     * and consist of Russian/English letters, spaces and digits.
     * @param name list name to check
     * @throws WrongListNameException if list name is illegal
     */
    private void checkListNameSpelling(@NotNull String name)throws WrongListNameException {
       if (name.isEmpty()) {
            throw new WrongListNameException("Enter list name.");
       } else if (!name.matches("[A-Za-zА-яа-я][A-Za-zА-яа-я0-9\\s]+")) {
            throw new WrongListNameException("List name should starts with letter and only contains letters and spaces.");
       }
    }

    /** Add new list into database. */
    private void addNewWordList(@NotNull String name) {
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
     * @throws WrongWordException if word is illegal
     */
    private void addNewWordIntoList(@NotNull String listName, @NotNull Word word) throws WrongWordException {
        WordFactory wordFactory = MainController.getGameController().getWordFactory();
        int wordId = wordFactory.addNewWord(word);
        getWritableDatabase().execSQL(
                "INSERT INTO " + getTableName(listName) +
                        "(" + WORD_ID_COLUMN + ") VALUES ('" + wordId + "')");
    }

    /**
     * Add new word list into a database with checking the ability of it.
     * @throws WrongListNameException if list name is illegal or such list already exists
     * @throws WrongWordException if some words in the list are illegal
     */
    public void addNewWordList(@NotNull String listName, @NotNull List<Word> wordList) throws WrongListNameException, WrongWordException {
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

    /**
     * Delete a list from the database.
     * If a list was current then random word list becomes current list.
     * @throws WrongListNameException if there is no such list
     */
    public void deleteWordList(@NotNull String name) throws WrongListNameException {
        if (!containsWordList(name)) {
            throw new WrongListNameException("No such word list.");
        }
        if (getWordListId(name) == getWordListId(getCurrentWordList())) {
            setCurrentWordList(RANDOM_WORD_LIST_NAME);
        }
        getWritableDatabase().execSQL("DROP TABLE " + getTableName(name));
        getWritableDatabase().execSQL("DELETE FROM " + WORD_LISTS_TABLE_NAME + " WHERE " + ID_COLUMN + " = " + getWordListId(name));
    }

    /**
     * Change a word list. Changing it's name and content.
     * @throws WrongWordException if some words in the list are illegal
     * @throws WrongListNameException if no such list exists or new list name is illegal
     */
    public void changeWordList(@NotNull String name,
                               @NotNull String newName,
                               @NotNull List<Word> newWords)
            throws WrongWordException, WrongListNameException {
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

    /**
     * Check if word in a list a not illegal.
     * @throws WrongWordException if some words in the list are illegal
     */
    private void checkWordsToAdd(@NotNull List<Word> words) throws WrongWordException {
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
