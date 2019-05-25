package ru.hse.android.project.easyenglish.controllers;

import android.content.Context;

import ru.hse.android.project.easyenglish.words.Word;
import ru.hse.android.project.easyenglish.words.WordFactory;

public class GameController {
    GameController(Context context) {
        wordFactory = new WordFactory(context);
        wordListController = new WordListController(context);
        wordStorage = new WordStorage();
        phrasesController = new PhrasesController(context);
        phaseStorage = new PhaseStorage();
    }

    void init() {
        wordListController.updateRandomWordList();
        wordStorage.updateStorage();
        phaseStorage.updateStorage();
    }

    private final WordStorage wordStorage;
    private final WordFactory wordFactory;
    private final WordListController wordListController;
    private final PhrasesController phrasesController;
    private final PhaseStorage phaseStorage;

    public void saveWordResult(Word word, boolean result) {
        wordFactory.saveWordStatistic(word, result);
    }

    public void resetStatistics(Word word) {
        wordFactory.resetStatistics(word);
    }

    public PhaseStorage getPhaseStorage() {
        return phaseStorage;
    }

    public PhrasesController getPhrasesController() {
        return phrasesController;
    }

    public WordFactory getWordFactory() {
        return wordFactory;
    }

    public WordListController getWordListController() {
        return wordListController;
    }

    public WordStorage getWordStorage() {
        return wordStorage;
    }
}
