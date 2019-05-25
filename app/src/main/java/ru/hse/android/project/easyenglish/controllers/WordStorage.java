package ru.hse.android.project.easyenglish.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.hse.android.project.easyenglish.words.Word;

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

    /**
     * Get a list of words of length min(number, maxLength).
     * maxLength -- current size of storage.
     * @param number requested length of list
     * @return list of words
     */
    public List<Word> getSetOfWords(int number) {
        if (number >= words.size()) {
            return new ArrayList<>(words);
        }
        List<Word> wordList = new ArrayList<>(number);
        for (int j = 0; j < number; j++) {
            wordList.add(words.get(i));
            i++;
            if (i == words.size()) {
                i = 0;
            }
        }
        return wordList;
    }
}
