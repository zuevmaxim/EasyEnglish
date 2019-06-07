package ru.hse.android.project.easyenglish.controllers;

import android.content.Context;

import org.jetbrains.annotations.NotNull;

import ru.hse.android.project.easyenglish.words.Word;
import ru.hse.android.project.easyenglish.words.WordFactory;

/**
 * Game controller contains all the other controllers(for databases) and storages.
 */
public class GameController {
    GameController(@NotNull Context context) {
        wordFactory = new WordFactory(context);
        wordStorage = new WordStorage();
        wordListController = new WordListController(context);
        phrasesController = new PhrasesController(context);
        phaseStorage = new PhraseStorage();
    }

    /**
     * Check if databases need initialisation.
     * Namely, random word list could be empty, or day list needs update.
     * Also loads lists into storages.
     */
    void init() {
        if (wordListController.needsInit()) {
            wordListController.updateRandomWordList();
        }
        if (wordListController.needsDayListInit()) {
            wordListController.updateDayList();
        }
        wordStorage.updateStorage();
        phaseStorage.updateStorage();
    }

    private final WordStorage wordStorage;
    private final WordFactory wordFactory;
    private final WordListController wordListController;
    private final PhrasesController phrasesController;
    private final PhraseStorage phaseStorage;

    /**
     * Save statistics about a word result in a game.
     * @param word word to update statistics
     * @param result game result
     */
    public void saveWordResult(@NotNull Word word, boolean result) {
        wordFactory.saveWordStatistic(word, result);
    }

    /** Reset all the statistics of the word. */
    public void resetStatistics(@NotNull Word word) {
        wordFactory.resetStatistics(word);
    }

    @NotNull
    public PhraseStorage getPhraseStorage() {
        return phaseStorage;
    }

    @NotNull
    public PhrasesController getPhrasesController() {
        return phrasesController;
    }

    @NotNull
    public WordFactory getWordFactory() {
        return wordFactory;
    }

    @NotNull
    public WordListController getWordListController() {
        return wordListController;
    }

    @NotNull
    public WordStorage getWordStorage() {
        return wordStorage;
    }
}
