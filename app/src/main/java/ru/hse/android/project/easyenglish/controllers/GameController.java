package ru.hse.android.project.easyenglish.controllers;

import android.content.Context;

import ru.hse.android.project.easyenglish.words.Word;
import ru.hse.android.project.easyenglish.words.WordFactory;

public class GameController {
    GameController(Context context) {
        wordFactory = new WordFactory(context);
        wordStorage = new WordStorage();
        wordListController = new WordListController(context);
        phrasesController = new PhrasesController(context);
        phaseStorage = new PhraseStorage();
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
    private final PhraseStorage phaseStorage;

    public void saveWordResult(Word word, boolean result) {
        wordFactory.saveWordStatistic(word, result);
    }

    public void resetStatistics(Word word) {
        wordFactory.resetStatistics(word);
    }

    public PhraseStorage getPhraseStorage() {
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
