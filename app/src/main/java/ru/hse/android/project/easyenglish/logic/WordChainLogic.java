package ru.hse.android.project.easyenglish.logic;

import android.support.annotation.NonNull;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import ru.hse.android.project.easyenglish.controllers.MainController;
import ru.hse.android.project.easyenglish.controllers.TranslateController;

/** Word chain game logic. */
public class WordChainLogic {

    /** Turn == 1 iff it is player's turn. */
    private int turn = 0;

    /** Game history. */
    private final LinkedHashSet<String> previousWords = new LinkedHashSet<>();

    /** Last opponent word used. */
    private String lastWord = "";

    /** Available number of hints. */
    private int hintsNumber = 3;

    /** Result word is acceptable. */
    public static final int RESULT_OK = 0;

    /** Result word has been used. */
    public static final int RESULT_REPETITION = 1;

    /** Result word is not a noun(or bad internet connection). */
    public static final int RESULT_NOT_A_NOUN = 2;

    /** Result empty word. */
    public static final int RESULT_EMPTY = 3;

    /** Result illegal first letter. */
    public static final int RESULT_WRONG_FIRST_LETTER = 4;

    /** Get history of the game. */
    @NonNull
    public LinkedHashSet<String> getPreviousWords() {
        return previousWords;
    }

    /** Change game turn. Should be used after makeMove. */
    public void changeTurn() {
        turn = 1 - turn;
    }

    /** Set turn on the start of the game. */
    public void setTurn(boolean turn) {
        this.turn = turn ? 1 : 0;
    }

    /** *Check if it is player's turn. */
    public boolean isMyTurn() {
        return turn == 1;
    }

    /** Check if word is a valid next move. */
    public int isValidMove(@NonNull String word) {
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

    /** Make a move in a game. */
    public void makeMove(@NonNull String word) {
        previousWords.add(word);
        if (!isMyTurn()) {
            lastWord = word;
        }
    }

    /** Hash a word in oder to send. */
    @NonNull
    public byte[] hash(@NonNull String word) {
        return word.getBytes(StandardCharsets.UTF_8);
    }

    /** Unhash a word after receiving. */
    @NonNull
    public String unhash(@NonNull byte[] message) {
        return new String(message, StandardCharsets.UTF_8);
    }

    /**
     * Get hints. Returns minimum of rest number of hints and available number of hints from the database.
     * List may be empty if internet connection is bad.
     * @return hints list
     */
    @NonNull
    public List<String> getHint() {
        List<String> words = MainController.getGameController().getWordFactory().getEnglishWordsStartsWithChar(lastWord.substring(lastWord.length() - 1));
        return words.stream().filter(s -> isValidMove(s) == RESULT_OK).limit(hintsNumber).collect(Collectors.toList());
    }

    /** Get rest number of hints. */
    public int getHintsNumber() {
        return hintsNumber;
    }

    /** Use a hind, decrease available number. */
    public void useHint() {
        hintsNumber = Math.max(0, hintsNumber - 1);
    }
}
