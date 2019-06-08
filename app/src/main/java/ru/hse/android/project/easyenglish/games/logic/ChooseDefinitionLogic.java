package ru.hse.android.project.easyenglish.games.logic;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import ru.hse.android.project.easyenglish.controllers.MainController;
import ru.hse.android.project.easyenglish.controllers.WordStorage;
import ru.hse.android.project.easyenglish.words.Word;

/**
 * Local game to memorize English words and their definitions.
 * Rules : You are given a word in English. Your task is to choose right Russian definition for it.
 */
public class ChooseDefinitionLogic {

    /** Max number of possible answers(translations) for English task word. */
    private final static int SIZE = 4;

    /** Word to choose definition for. */
    private Word answer;

    /** List of words with right and other random possible definitions. */
    private List<Word> possibleAnswers;

    /** Hint for this game - odd word. */
    private Word hint;

    private final Random random = new Random();

    private final static WordStorage wordStorage = MainController.getGameController().getWordStorage();

    public ChooseDefinitionLogic() {
        update();
    }

    /** Choose new word and possible answers for it. */
    public void update() {
        possibleAnswers = wordStorage.getSetOfWords(SIZE);
        answer = possibleAnswers.get(0);
        Collections.shuffle(possibleAnswers);
        hint = generateHint();
    }

    public Word getAnswer() {
        return answer;
    }

    public List<Word> getPossibleAnswers() {
        return possibleAnswers;
    }

    public Word getHint() {
        return hint;
    }

    /** Generate hint - odd word. */
    private Word generateHint() {
        Word hint = possibleAnswers.get(0);
        while (!hint.equals(answer)) {
            hint = possibleAnswers.get(random.nextInt(possibleAnswers.size()));
        }
        return hint;
    }

    /** Check if given answer equals to actual answer and set statistics. */
    public boolean checkAnswer(Word givenAnswer) {
        boolean result = (givenAnswer.equals(answer));
        MainController.getGameController().saveWordResult(answer, result);
        return result;
    }
}
