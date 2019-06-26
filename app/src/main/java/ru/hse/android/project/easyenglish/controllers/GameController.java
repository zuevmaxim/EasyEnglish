package ru.hse.android.project.easyenglish.controllers;

import android.content.Context;
import android.support.annotation.NonNull;

import ru.hse.android.project.easyenglish.words.Word;

/**
 * Game controller contains all the other controllers(for databases) and storages.
 */
public class GameController {

    private final WordStorage wordStorage;
    private final WordFactory wordFactory;
    private final WordListController wordListController;
    private final PhrasesController phrasesController;
    private final PhraseStorage phaseStorage;

    GameController(@NonNull Context context) {
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

    /**
     * Save statistics about a word result in a game.
     * @param word word to update statistics
     * @param result game result
     */
    public void saveWordResult(@NonNull Word word, boolean result) {
        wordFactory.saveWordStatistic(word, result);
    }

    @NonNull
    public PhraseStorage getPhraseStorage() {
        return phaseStorage;
    }

    @NonNull
    public PhrasesController getPhrasesController() {
        return phrasesController;
    }

    @NonNull
    public WordFactory getWordFactory() {
        return wordFactory;
    }

    @NonNull
    public WordListController getWordListController() {
        return wordListController;
    }

    @NonNull
    public WordStorage getWordStorage() {
        return wordStorage;
    }
}
