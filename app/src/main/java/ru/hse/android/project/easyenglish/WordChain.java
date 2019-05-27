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

    public String data = "";
    public int turnCounter = 0;

    private static final String WORD_LABEL = "word";
    private static final String STATUS_LABEL = "status";
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

    // This is the byte array we will write out to the TBMP API.
    public byte[] persist() {
        JSONObject retVal = new JSONObject();

        try {
            retVal.put("data", data);
            retVal.put("turnCounter", turnCounter);

        } catch (JSONException e) {
            Log.e("SkeletonTurn", "There was an issue writing JSON!", e);
        }

        String st = retVal.toString();

        Log.d(TAG, "==== PERSISTING\n" + st);

        return st.getBytes(Charset.forName("UTF-8"));
    }

    // Creates a new instance of SkeletonTurn.
    static public WordChain unpersist(byte[] byteArray) {

        if (byteArray == null) {
            Log.d(TAG, "Empty array---possible bug.");
            return new WordChain();
        }

        String st = null;
        try {
            st = new String(byteArray, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            return null;
        }

        Log.d(TAG, "====UNPERSIST \n" + st);

        WordChain retVal = new WordChain();

        try {
            JSONObject obj = new JSONObject(st);

            if (obj.has("data")) {
                retVal.data = obj.getString("data");
            }
            if (obj.has("turnCounter")) {
                retVal.turnCounter = obj.getInt("turnCounter");
            }

        } catch (JSONException e) {
            Log.e("SkeletonTurn", "There was an issue parsing JSON!", e);
        }

        return retVal;
    }
}
