package ru.hse.android.project.easyenglish.logic;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ru.hse.android.project.easyenglish.controllers.MainController;
import ru.hse.android.project.easyenglish.controllers.PhraseStorage;
import ru.hse.android.project.easyenglish.words.Phrase;

/**
 * Local game to memorize common English phrases.
 * Rules : You are given phrase in English with shuffled words. Your task is to put words in right order and write down the result.
 */
public class WordPuzzleLogic {

    /** Phrase task. */
    private Phrase phrase;

    /** List of words in phrase task. */
    private List<String> answer;

    /** Shuffled words in phrase task. */
    private List<String> shuffledAnswer;

    private final PhraseStorage storage = MainController.getGameController().getPhraseStorage();

    /** Choose new phrase and generate task. */
    public void update() {
        phrase = storage.nextPhrase();
        answer = Arrays.asList(phrase.getEnglish().split(" "));
        shuffledAnswer = shuffleAnswer();
    }

    @NonNull
    public List<String> getShuffledAnswer() {
        return shuffledAnswer;
    }

    @NonNull
    public String getHint(int position) {
        return answer.get(position);
    }

    @NonNull
    public Phrase getAnswer() {
        return phrase;
    }

    /** Check if words in given answer are in right order. */
    public boolean checkAnswer(@NonNull List<String> givenAnswer) {
        return givenAnswer.equals(answer);
    }

    /** Generate shuffled word list from given until lists are not equals. */
    @NonNull
    private List<String> shuffleAnswer() {
        List<String> shuffledWordResult = new ArrayList<>(answer);
        while (answer.equals(shuffledWordResult) && shuffledWordResult.size() > 1) {
            Collections.shuffle(shuffledWordResult);
        }
        return shuffledWordResult;
    }
}
