package ru.hse.android.project.easyenglish;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import ru.hse.android.project.easyenglish.controllers.MainController;
import ru.hse.android.project.easyenglish.controllers.TranslateController;

public class WordChain {

    private int turn = 0;
    private final LinkedHashSet<String> previousWords = new LinkedHashSet<>();
    private String lastWord = "";
    private int hintsNumber = 3;

    static final int RESULT_OK = 0;
    static final int RESULT_REPETITION = 1;
    static final int RESULT_NOT_A_NOUN = 2;
    static final int RESULT_EMPTY = 3;
    static final int RESULT_WRONG_FIRST_LETTER = 4;


    LinkedHashSet<String> getPreviousWords() {
        return previousWords;
    }

    void changeTurn() {
        turn = 1 - turn;
    }

    void setTurn(boolean turn) {
        this.turn = turn ? 1 : 0;
    }

    boolean isMyTurn() {
        return turn == 1;
    }

    int isValidMove(String word) {
        if (previousWords.contains(word)) {
            return RESULT_REPETITION;
        } else if (word.isEmpty()) {
            return RESULT_EMPTY;
        } else if (!lastWord.isEmpty() && lastWord.charAt(lastWord.length() - 1) != word.charAt(0)) {
            return RESULT_WRONG_FIRST_LETTER;
        } else if (!TranslateController.wordInfo(word).isNoun()) {
            return RESULT_NOT_A_NOUN;
        } else {
            return RESULT_OK;
        }
    }

    void makeMove(String word) {
        previousWords.add(word);
        if (!isMyTurn()) {
            lastWord = word;
        }
    }

    byte[] hash(String word) {
        return word.getBytes(StandardCharsets.UTF_8);
    }

    String unhash(@NotNull byte[] message) {
        return new String(message, StandardCharsets.UTF_8);
    }

    List<String> getHint() {
        List<String> words = MainController.getGameController().getWordFactory().getEnglishWordsStartsWithChar(lastWord.substring(0, 1));
        return words.stream().filter(s -> isValidMove(s) == RESULT_OK).limit(hintsNumber).collect(Collectors.toList());
    }

    int getHintsNumber() {
        return hintsNumber;
    }

    void useHint() {
        hintsNumber = Math.max(0, hintsNumber - 1);
    }
}
