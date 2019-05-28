package ru.hse.android.project.easyenglish;

import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;

import ru.hse.android.project.easyenglish.controllers.TranslateController;

public class WordChain {
    public class WordChainException extends Exception {}

    public static final String TAG = "WORD_CHAIN";

    private int turn = 0;
    private LinkedHashSet<String> previousWords = new LinkedHashSet<>();
    private String lastWord = "";

    public static final int RESULT_OK = 0;
    public static final int RESULT_REPETITION = 1;
    public static final int RESULT_NOT_A_NOUN = 2;
    public static final int RESULT_EMPTY = 3;
    public static final int RESULT_WRONG_FIRST_LETTER = 4;


    public LinkedHashSet<String> getPreviousWords() {
        return previousWords;
    }

    public void changeTurn() {
        turn = 1 - turn;
    }

    public void setTurn(boolean turn) {
        this.turn = turn ? 1 : 0;
    }

    public boolean isMyTurn() {
        return turn == 1;
    }

    public int isValidMove(String word) { //TODO should return error code
        if (previousWords.contains(word)) {
            return RESULT_REPETITION;
        } else if (!TranslateController.wordInfo(word).isNoun()) {
            return RESULT_NOT_A_NOUN;
        } else if (word.isEmpty()) {
            return RESULT_EMPTY;
        } else if (!lastWord.isEmpty() && lastWord.charAt(lastWord.length() - 1) != word.charAt(0)) {
            return RESULT_WRONG_FIRST_LETTER;
        } else {
            return RESULT_OK;
        }
    }

    public void makeMove(String word) {
        previousWords.add(word);
    }

    public byte[] hash(String word) {
        return word.getBytes(StandardCharsets.UTF_8);
    }

    public String unhash(@NotNull byte[] message) {
        return new String(message, StandardCharsets.UTF_8);
    }

 }
