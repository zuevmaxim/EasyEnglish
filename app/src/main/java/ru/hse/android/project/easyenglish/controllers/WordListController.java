package ru.hse.android.project.easyenglish.controllers;


import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ru.hse.android.project.easyenglish.R;
import ru.hse.android.project.easyenglish.controllers.db.ListsDB;
import ru.hse.android.project.easyenglish.exceptions.WrongListNameException;
import ru.hse.android.project.easyenglish.exceptions.WrongWordException;
import ru.hse.android.project.easyenglish.words.Word;

/**
 * Controller for working with word lists.
 */
public class WordListController {

    private static final int RANDOM_WORD_LIST_LENGTH = 20;
    private static final int DAY_LIST_LENGTH = 10;
    private static final int PREF_NEW_WORDS = 7;
    private static final String CURRENT_VALUE =  "1";
    private static final String NOT_CURRENT_VALUE =  "0";

    private final ListsDB listsDB;
    
    private final Context context;
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

    /**
     * Constructor loads database if needed
     * and upgrade it if application reinstalled and database version has changed.
     */
    WordListController(@NonNull Context context) {
        listsDB = new ListsDB(context);
        this.context = context;
    }

    /**
     * Find list's id by name.
     * @param name list name
     * @return list id or 0 if no such list exists
     */
    public int getWordListId(@NonNull String name) {
        return listsDB.getWordListId(name);
    }

    /** Get id of the random word list. */
    public int getRandomWordListId() {
        return getWordListId(ListsDB.RANDOM_WORD_LIST_NAME);
    }

    /** Get id of the day list. */
    public int getDayListId() {
        return getWordListId(ListsDB.DAY_LIST_NAME);
    }

    /**
     * Check if random word list is empty.
     * @return true iff random word list is empty
     */
    boolean needsInit() {
        return listsDB.isEmptyList(ListsDB.RANDOM_WORD_LIST_NAME);
    }

    /**
     * Check if day list needs init.
     * @return true iff day list is outdated
     */
    boolean needsDayListInit() {
        String currentDate = format.format(System.currentTimeMillis());
        return listsDB.isEmptyList(ListsDB.DAY_LIST_NAME) || !listsDB.getListDate(ListsDB.DAY_LIST_NAME).equals(currentDate);
    }

    /**
     * Update random word list.
     * Generate new random set of words.
     * And update word storage.
     */
    public void updateRandomWordList() {
        WordFactory wordFactory = MainController.getGameController().getWordFactory();
        List<Integer> ids = wordFactory.nextWordIds(RANDOM_WORD_LIST_LENGTH);
        listsDB.updateRandomWordList(ids);
        MainController.getGameController().getWordStorage().updateStorage();
    }

    /**
     * Update day list, and update word storage.
     * Generates a new word list, where some words are new and the others are with bad statistics.
     */
    public void updateDayList() {
        String currentDate = format.format(System.currentTimeMillis());
        listsDB.setListDate(ListsDB.DAY_LIST_NAME, currentDate);
        WordFactory wordFactory = MainController.getGameController().getWordFactory();
        List<Integer> ids = wordFactory.generateDayList(DAY_LIST_LENGTH, PREF_NEW_WORDS);
        listsDB.updateDayList(ids);
        MainController.getGameController().getWordStorage().updateStorage();
    }

    /** Get a list of names of word lists. */
    @NonNull
    public List<String> getWordLists() {
        return listsDB.getWordLists();
    }

    /** Get words of the current word list. */
    @NonNull
    public List<Word> getCurrentListWords() {
        return getListWords(getCurrentWordList());
    }

    /** Get words of the specified word list. */
    @NonNull
    public List<Word> getListWords(@NonNull String wordListName) {
        WordFactory wordFactory = MainController.getGameController().getWordFactory();
        List<Word> words = new ArrayList<>();
        List<Integer> ids = listsDB.getListWords(wordListName);
        for (int id : ids) {
            words.add(wordFactory.getWordById(id));
        }
        return words;
    }

    /** Get name of the current word list. */
    @NonNull
    public String getCurrentWordList() {
        return listsDB.getCurrentWordList();
    }

    /** Set new current word list. */
    public void setCurrentWordList(@NonNull String newListName) {
        String currentListName = getCurrentWordList();
        listsDB.setCurrentValue(currentListName, NOT_CURRENT_VALUE);
        listsDB.setCurrentValue(newListName, CURRENT_VALUE);
        MainController.getGameController().getWordStorage().updateStorage();
    }

    /** Set day list as current. */
    public void setCurrentDayList() {
        setCurrentWordList(ListsDB.DAY_LIST_NAME);
    }

    /** Check if database contains such a list. */
    private boolean containsWordList(@NonNull String listName) {
        return listsDB.containsWordList(listName);
    }

    /**
     * Check list name. Name of a list should be non-empty
     * and consist of Russian/English letters, spaces and digits.
     * @param name list name to check
     * @throws WrongListNameException if list name is illegal
     */
    private void checkListNameSpelling(@NonNull String name)throws WrongListNameException {
       if (name.isEmpty()) {
            throw new WrongListNameException(context.getString(R.string.empty_list_name_error));
       } else if (!name.matches("[A-Za-zА-яа-я][A-Za-zА-яа-я0-9\\s]*")) {
            throw new WrongListNameException(context.getString(R.string.wrong_word_format_error));
       }
    }

    /**
     * Add new word list into a database with checking the ability of it.
     * @throws WrongListNameException if list name is illegal or such list already exists
     * @throws WrongWordException if some words in the list are illegal
     */
    public void addNewWordList(@NonNull String listName, @NonNull List<Word> wordList) throws WrongListNameException, WrongWordException {
        if (containsWordList(listName)) {
            throw new WrongListNameException(context.getString(R.string.list_already_exists_error));
        }
        checkListNameSpelling(listName);
        checkWordsToAdd(wordList);
        WordFactory wordFactory = MainController.getGameController().getWordFactory();
        List<Integer> ids = wordList.stream().map(word -> {
            try {
                return wordFactory.addNewWord(word);
            } catch (WrongWordException ignored) { }
            return 0;
        }).collect(Collectors.toList());
        listsDB.addNewWordList(listName, ids);
    }

    /**
     * Delete a list from the database.
     * If a list was current then random word list becomes current list.
     * @throws WrongListNameException if there is no such list
     */
    public void deleteWordList(@NonNull String name) throws WrongListNameException {
        if (!containsWordList(name)) {
            throw new WrongListNameException(context.getString(R.string.no_such_list_error) + ".");
        }
        if (getWordListId(name) == getWordListId(getCurrentWordList())) {
            setCurrentWordList(ListsDB.RANDOM_WORD_LIST_NAME);
        }
        listsDB.deleteWordList(name);
    }

    /**
     * Change a word list. Changing it's name and content.
     * @throws WrongWordException if some words in the list are illegal
     * @throws WrongListNameException if no such list exists or new list name is illegal
     */
    public void changeWordList(@NonNull String name,
                               @NonNull String newName,
                               @NonNull List<Word> newWords)
            throws WrongWordException, WrongListNameException {
        if (!containsWordList(name)) {
            throw new WrongListNameException(context.getString(R.string.no_such_list_error) + name + ".");
        }
        checkListNameSpelling(newName);
        if (!name.equals(newName) && containsWordList(name)) { //same names is OK
            throw new WrongListNameException(context.getString(R.string.list_already_exists_error));
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
    private void checkWordsToAdd(@NonNull List<Word> words) throws WrongWordException {
        WordFactory wordFactory = MainController.getGameController().getWordFactory();
        List<Word> listWithoutDuplicates = words.stream().distinct().collect(Collectors.toList());
        if (!words.equals(listWithoutDuplicates)) {
            throw new WrongWordException(context.getString(R.string.dupplicate_word_error));
        }

        for (Word word : words) {
            wordFactory.checkWordSpelling(word);
        }
    }
}
