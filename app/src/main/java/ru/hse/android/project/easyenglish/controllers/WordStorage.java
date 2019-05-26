package ru.hse.android.project.easyenglish.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import ru.hse.android.project.easyenglish.words.Word;
import ru.hse.android.project.easyenglish.words.WordFactory;

public class WordStorage {
    private static final Random RANDOM = new Random();
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
        Word word1 = words.get(i);
        Word word2 = getMinimal();
        if (word1 == word2 || RANDOM.nextBoolean()) {
            i++;
            if (i == words.size()) {
                Collections.shuffle(words);
                i = 0;
            }
            return word1;
        } else {
            return word2;
        }
    }

    /**
     * Get a list of words of length min(number, maxLength).
     * maxLength -- current size of storage.
     * @param number requested length of list
     * @return list of words
     */
    public List<Word> getSetOfWords(int number) {
        Word min = nextWord();
        if (number >= words.size()) {
            List<Word> result = new ArrayList<>(words);
            result.remove(min);
            result.add(0, min);
            return result;
        }
        List<Word> wordList = new ArrayList<>(number);
        for (int j = 0; j < number; j++) {
            wordList.add(words.get(i));
            i++;
            if (i == words.size()) {
                i = 0;
            }
        }
        if (!wordList.contains(min)) {
            wordList.set(0, min);
        } else {
            wordList.remove(min);
            wordList.add(0, min);
        }
        return wordList;
    }

    private Word getMinimal() {
        WordFactory wordFactory = MainController.getGameController().getWordFactory();
        return words.stream().min((a, b) -> {
            int totalA = wordFactory.getWordTotalNumber(a);
            int totalB = wordFactory.getWordTotalNumber(b);
            double errorA = wordFactory.getWordErrorNumber(a);
            double errorB = wordFactory.getWordErrorNumber(b);
            if (totalA == 0) {
                return -1;
            }
            if (totalB == 0) {
                return 1;
            }
            double result = errorA / totalA - errorB / totalB;
            return result == 0 ? 0 : (result < 0 ? -1 : 1);
        }).orElse(new Word("ошибка", "error"));
    }
}
