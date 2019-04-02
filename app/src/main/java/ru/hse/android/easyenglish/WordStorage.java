package ru.hse.android.easyenglish;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WordStorage {
    WordStorage() {
    }

    private List<String> words = new ArrayList<>();
    private int i = 0;

    void updateStorage(Context context) {
        words = MainController.getGameController().getWordListController(context).getCurrentListWords();
        Collections.shuffle(words);
    }

    public String nextWord() {
        String word = words.get(i);
        i++;
        if (i == words.size()) {
            Collections.shuffle(words);
            i = 0;
        }
        return word;
    }
}
