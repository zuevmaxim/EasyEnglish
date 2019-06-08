package ru.hse.android.project.easyenglish.games.logic;

import android.content.Intent;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import ru.hse.android.project.easyenglish.controllers.MainController;
import ru.hse.android.project.easyenglish.controllers.TranslateController;
import ru.hse.android.project.easyenglish.controllers.WordStorage;
import ru.hse.android.project.easyenglish.words.Word;

public class SynonymsLogic {

    /** Max number of possible answers(synonyms) for English task word. */
    private static final int SIZE = 5;

    /** Word to choose synonyms. */
    private Word wordTask;

    /** List of synonyms to word task. */
    private List<String> answer;

    /** List of possible answers(synonyms anr not) to word task*/
    private List<String> possibleAnswers;

    /** Hint for this game - odd word in possible answers list. */
    private String hint;

    private final Random random = new Random();

    private final static WordStorage wordStorage = MainController.getGameController().getWordStorage();

    public SynonymsLogic() {
        update();
    }

    /** Choose new word and possible answers for it. */
    public boolean update() {
        final List<Word> words = wordStorage.getSetOfWords(SIZE);
        wordTask = words.remove(0);

        answer = TranslateController.getSynonyms(wordTask.getEnglish());
        answer = answer.subList(0, Math.min(random.nextInt(3) + 1, answer.size()));
        possibleAnswers = new ArrayList<>(answer);

        if (answer == null) {
            return false;
        }

        for (Word word : words) {
            if (possibleAnswers.size() > SIZE) {
                continue;
            }
            if (!possibleAnswers.contains(word.getEnglish())) {
                possibleAnswers.add(word.getEnglish());
            }
        }

        hint = generateHint();
        Collections.shuffle(possibleAnswers);
        return true;
    }

    public List<String> getPossibleAnswers() {
        return possibleAnswers;
    }

    public Word getWordTask() {
        return wordTask;
    }

    public String getHint() {
        return hint;
    }

    public List<String> getAnswer() {
        return answer;
    }

    /** Generate hint - odd word in possible answer list. */
    private String generateHint() {
        for (String word : possibleAnswers) {
            if (!answer.contains(word)) {
                return word;
            }
        }
        return null;
    }

    /** Check if given list or words are synonyms to word task and set statistics. */
    public boolean checkAnswer(List<String> givenAnswer) {
        boolean result = givenAnswer.size() == answer.size();
        for (String word : givenAnswer) {
            result &= answer.contains(word);
        }
        MainController.getGameController().saveWordResult(wordTask, result);
        return result;
    }
}
