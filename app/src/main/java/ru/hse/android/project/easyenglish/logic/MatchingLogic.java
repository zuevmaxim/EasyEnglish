package ru.hse.android.project.easyenglish.logic;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import ru.hse.android.project.easyenglish.controllers.MainController;
import ru.hse.android.project.easyenglish.controllers.WordStorage;
import ru.hse.android.project.easyenglish.words.Word;

/**
 * Local game to memorize English words and their definitions.
 * Rules : You are given words in Russian and their shuffles English translations. Your task match English words with their definitions.
 */
public class MatchingLogic {

    /** Max number of possible answers(pairs). */
    private final static int SIZE = 4;

    /** List of word pairs - matching. */
    private List<Word> answer;

    /** Shuffled English translations from list words. */
    private List<String> shuffledEnglishWords;

    /** Hint for this game is word - pair of russian word and its translation. */
    private Word hint;

    private final Random random = new Random();

    private final WordStorage wordStorage = MainController.getGameController().getWordStorage();

    /** Choose pairs of words and generate task. */
    public void update() {
        answer = wordStorage.getSetOfWords(SIZE);
        shuffledEnglishWords = shuffleEnglishWords();
        hint = generateHint();
    }

    @NonNull
    public List<String> getRussianWords() {
        return answer.stream().map(Word::getRussian).collect(Collectors.toList());
    }

    @NonNull
    public List<String> getShuffledEnglishWords() {
        return shuffledEnglishWords;
    }

    @NonNull
    public Word getHint() {
        return hint;
    }

    @NonNull
    public List<Word> getAnswer() {
        return answer;
    }

    /** Generate hint - right matched pair. */
    @NonNull
    private Word generateHint() {
        return answer.get(random.nextInt(answer.size()));
    }

    /** Check if given answer is a right matching. */
    public boolean checkAnswer(@NonNull List<String> givenAnswer) {
        return givenAnswer.equals(answer.stream().map(Word::getEnglish).collect(Collectors.toList()));
    }

    /** Generate shuffled word list from given until lists are not equals. */
    @NonNull
    private List<String> shuffleEnglishWords() {
        final List<String> englishWords = answer.stream().map(Word::getEnglish).collect(Collectors.toList());
        final List<String> shuffledEnglishWords = new ArrayList<>(englishWords);
        while (englishWords.equals(shuffledEnglishWords) && englishWords.size() > 1) {
            Collections.shuffle(shuffledEnglishWords);
        }
        return shuffledEnglishWords;
    }
}
