package ru.hse.android.project.easyenglish.games.logic;

import android.support.annotation.NonNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ru.hse.android.project.easyenglish.controllers.MainController;
import ru.hse.android.project.easyenglish.words.Word;

/**
 * Local game to memorize English words and their spelling.
 * Rules : You are given word in English with shuffled letters. Your task is to put letters in right order and write down the result.
 */
public class LetterPuzzleLogic {

    /** Answer for word in English with shuffled letters. */
    private Word answer;

    /** English word with shuffled. */
    private String shuffledAnswer;

    /** Hint for this game - first letter. */
    private char hint;

    /** Get new answer. */
    public void update() {
        answer = MainController.getGameController().getWordStorage().nextWord();
        shuffledAnswer = shuffleLetters();
        hint = generateHint();
    }

    @NonNull
    public Word getAnswer() {
        return answer;
    }

    @NonNull
    public String getShuffledAnswer() {
        return shuffledAnswer;
    }

    public char getHint() {
        return hint;
    }

    /** Generate word with shuffled letters from given until they are not equals. */
    @NonNull
    private String shuffleLetters() {
        String word = answer.getEnglish();
        List<String> letters = Arrays.asList(word.split(""));
        String shuffledWordResult = word;
        while (shuffledWordResult.equals(word) && word.length() > 1) {
            StringBuilder shuffledWord = new StringBuilder();
            Collections.shuffle(letters);
            for (String letter : letters) {
                shuffledWord.append(letter);
            }
            shuffledWordResult = shuffledWord.toString();
        }
        return shuffledWordResult;
    }

    /** Generate hint - first letter. */
    private char generateHint() {
        return answer.getEnglish().charAt(0);
    }

    /** Check if given answer equals to model and set statistic. */
    public boolean checkAnswer(@NonNull String givenAnswer) {
        boolean result = givenAnswer.equals(answer.getEnglish());
        MainController.getGameController().saveWordResult(answer, result);
        return result;
    }
}
