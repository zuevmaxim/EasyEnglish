package ru.hse.android.easyenglish;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class WordFactory {
    WordFactory() {

    }

    private static final List<String> words = Arrays.asList(
            "кошка", "собака", "проводник", "кислота", "молоко", "кафтан", "багет");

    private static final Random random = new Random();

    public String nextWord() {
        return words.get(random.nextInt(words.size()));
    }
}
