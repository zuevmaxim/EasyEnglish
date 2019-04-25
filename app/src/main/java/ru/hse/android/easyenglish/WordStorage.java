package ru.hse.android.easyenglish;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WordStorage {
    WordStorage() {
    }

    private List<Word> words = new ArrayList<>();
    private int i = 0;

    void updateStorage() {
        i = 0;
        words = MainController.getGameController().getWordListController().getCurrentListWords();
        Collections.shuffle(words);
    }

    public Word nextWord() {
        Word word = words.get(i);
        i++;
        if (i == words.size()) {
            Collections.shuffle(words);
            i = 0;
        }
        return word;
    }
}
