package ru.hse.android.project.easyenglish.controllers;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

import ru.hse.android.project.easyenglish.R;
import ru.hse.android.project.easyenglish.controllers.db.WordsDB;
import ru.hse.android.project.easyenglish.exceptions.WrongWordException;
import ru.hse.android.project.easyenglish.words.Word;

/** A database for holding words and statistics. */
public class WordFactory {

    private final WordsDB wordsDB;
    private final Context context;

    public WordFactory(@NonNull Context context) {
        wordsDB = new WordsDB(context);
        this.context = context;
    }

    /** Get a random set of words of specified length. */
    @NonNull
    public List<Integer> nextWordIds(int length) {
        return wordsDB.nextWordIds(length);
    }

    /**
     * Update day list.
     * @param length length of the list
     * @param newWordsLength preference number of new words
     */
    @NonNull
    public List<Integer> generateDayList(int length, int newWordsLength) {
        List<Integer> result = wordsDB.getZeroWords(newWordsLength);
        int rest = length - result.size();
        result.addAll(wordsDB.getMinimalList(rest));
        return result;
    }

    /** Get a word by it's id. */
    @NonNull
    public Word getWordById(int id) {
        return wordsDB.getWordById(id);
    }

    /** Set word's statistics to zero. */
    public void resetStatistics(@NonNull Word word) {
        wordsDB.setStatistics(word, 0 , 0 , 0);
    }

    /** Get number of errors for the word. */
    public int getWordErrorNumber(@NonNull Word word) {
        return wordsDB.getWordErrorNumber(word);
    }

    /** Get totatl number of tests for the word. */
    public int getWordTotalNumber(@NonNull Word word) {
        return wordsDB.getWordTotalNumber(word);
    }

    /**
     * Save result of a game for the word.
     * @param result true iff was given a correct answer
     */
    public void saveWordStatistic(@NonNull Word word, boolean result) {
        int previousErrorResult = getWordErrorNumber(word);
        int previousTotalResult = getWordTotalNumber(word);
        int error = result ? previousErrorResult : previousErrorResult + 1;
        int total = previousTotalResult + 1;
        double percent = 1 - (double)error / total;

        wordsDB.setStatistics(word, total, error, percent);
    }

    /**
     * Check that word's spelling is legal.
     * @throws WrongWordException if spelling is illegal
     */
    public void checkWordSpelling(@NonNull Word word) throws WrongWordException {
        checkEnglishSpelling(word.getEnglish());
        checkRussianSpelling(word.getRussian());
    }

    /**
     * Check that word's spelling is legal.
     * Word should consists of Russian letters, spaces and some separators.
     * Word should not be empty.
     * @throws WrongWordException if spelling is illegal
     */
    public void checkRussianSpelling(@NonNull String russianWord) throws WrongWordException {
        if (russianWord.isEmpty()) {
            throw new WrongWordException(context.getString(R.string.error_empty_word));
        } else if (!russianWord.matches("[А-Яа-я,.!?\\-\\s]+")) {
            throw new WrongWordException(context.getString(R.string.wrong_word_symbol));
        }
    }

    /**
     * Check that word's spelling is legal.
     * Word should consists of English letters, spaces and some separators.
     * Word should not be empty.
     * @throws WrongWordException if spelling is illegal
     */
    public void checkEnglishSpelling(@NonNull String englishWord) throws WrongWordException {
        if (englishWord.isEmpty()) {
            throw new WrongWordException(context.getString(R.string.error_empty_word));
        }
        if (!englishWord.matches("[A-Za-z,.!?\\-\\s]+")) {
            throw new WrongWordException(context.getString(R.string.wrong_word_symbol));
        }
    }

    /**
     * Add a word into the database.
     * @param word word to add
     * @return word's id in the database
     * @throws WrongWordException if spelling is illegal
     */
    public int addNewWord(@NonNull Word word) throws WrongWordException {
        checkWordSpelling(word);

        if (wordsDB.containsWord(word)) {
            return wordsDB.getWordId(word);
        } else {
            return wordsDB.addNewWord(word);
        }
    }

    /** Check if the database already contains such word. */
    public boolean containsWord(@NonNull Word word) {
        return wordsDB.containsWord(word);
    }

    /**
     * Get a list of words, which start with specified prefix.
     * It is used in WordChain hints.
     */
    @NonNull
    public List<String> getEnglishWordsStartsWithChar(@NonNull String prefix) {
        return wordsDB.getEnglishWordsStartsWithChar(prefix);
    }

    public void deleteWord(@NonNull Word word) {
        wordsDB.deleteWord(word);
    }
}
