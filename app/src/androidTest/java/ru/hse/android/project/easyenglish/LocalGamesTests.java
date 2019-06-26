package ru.hse.android.project.easyenglish;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import ru.hse.android.project.easyenglish.controllers.MainController;
import ru.hse.android.project.easyenglish.exceptions.WrongListNameException;
import ru.hse.android.project.easyenglish.exceptions.WrongWordException;
import ru.hse.android.project.easyenglish.logic.ChooseDefinitionLogic;
import ru.hse.android.project.easyenglish.logic.LetterPuzzleLogic;
import ru.hse.android.project.easyenglish.logic.MatchingLogic;
import ru.hse.android.project.easyenglish.logic.SynonymsLogic;
import ru.hse.android.project.easyenglish.logic.WordPuzzleLogic;
import ru.hse.android.project.easyenglish.words.Phrase;
import ru.hse.android.project.easyenglish.words.Word;

import static org.junit.Assert.*;

/**
 * Tests for local games and databases.
 */
@RunWith(AndroidJUnit4.class)
public class LocalGamesTests {

    private static final String LETTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final Random RANDOM = new Random();
    private static String suffix = "";
    private static final int SUFFIX_LENGTH = 20;

    private static List<Word> testList;
    private static final String TEST_LIST = "LocalGameTest";

    @BeforeClass
    public static void initMainController() throws WrongListNameException, WrongWordException {
        Context appContext = InstrumentationRegistry.getTargetContext();
        MainController.init(appContext);
        for (int i = 0; i < SUFFIX_LENGTH; i++) {
            suffix += LETTERS.charAt(RANDOM.nextInt(LETTERS.length()));
        }

        testList = new ArrayList<>(Arrays.asList(
                new Word ("первоеслово", "firstword" + suffix),
                new Word ("второеслово", "secondword" + suffix),
                new Word ("третьеслово", "thirdword" + suffix),
                new Word ("четвертоеслово", "fourthword" + suffix),
                new Word ("пятоеслово", "fifthword" + suffix),
                new Word ("шестоеслово", "sixthword" + suffix)));
        MainController.getGameController().getWordListController().addNewWordList(TEST_LIST, testList);
        MainController.getGameController().getWordListController().setCurrentWordList(TEST_LIST);
    }

    @AfterClass
    public static void deleteTestList() throws WrongListNameException {
        MainController.getGameController().getWordListController().deleteWordList(TEST_LIST);
        for (Word word : testList) {
            MainController.getGameController().getWordFactory().deleteWord(word);
            assertFalse(MainController.getGameController().getWordFactory().containsWord(word));
        }
    }

    @Test
    public void initListTest() {
        assertEquals(TEST_LIST, MainController.getGameController().getWordListController().getCurrentWordList());
        for (Word word : testList) {
            assertTrue(MainController.getGameController().getWordFactory().containsWord(word));
        }
    }

    @Test
    public void chooseDefinitionTest() {
        ChooseDefinitionLogic game = new ChooseDefinitionLogic();
        game.update();
        List<Word> possibleAnswers = game.getPossibleAnswers();

        for (Word word : possibleAnswers) {
            assertTrue(testList.contains(word));
        }

        Word answer = game.getAnswer();
        assertTrue(possibleAnswers.contains(answer));
        assertFalse(game.checkAnswer(new Word("седьмоеслово", "seventhword")));

        for (Word word : possibleAnswers) {
            if (word.equals(answer)) {
                assertTrue(game.checkAnswer(word));
            } else {
                assertFalse(game.checkAnswer(word));
            }
        }
    }


    @Test
    public void letterPuzzleTest() {
        LetterPuzzleLogic game = new LetterPuzzleLogic();
        game.update();
        Word answer = game.getAnswer();
        String shuffled = game.getShuffledAnswer();
        char hint = game.getHint();

        int previousTotal = MainController.getGameController().getWordFactory().getWordTotalNumber(answer);
        int previousError = MainController.getGameController().getWordFactory().getWordErrorNumber(answer);

        assertEquals(hint, answer.getEnglish().charAt(0));
        assertTrue(MainController.getGameController().getWordListController().getCurrentListWords().contains(answer));
        assertFalse(game.checkAnswer(shuffled));
        assertEquals(previousTotal + 1, MainController.getGameController().getWordFactory().getWordTotalNumber(answer));
        assertEquals(previousError + 1, MainController.getGameController().getWordFactory().getWordErrorNumber(answer));
        assertTrue(game.checkAnswer(answer.getEnglish()));
        assertEquals(previousTotal + 2, MainController.getGameController().getWordFactory().getWordTotalNumber(answer));
        assertEquals(previousError + 1, MainController.getGameController().getWordFactory().getWordErrorNumber(answer));

    }

    @Test
    public void matchingTest() {
        MatchingLogic game = new MatchingLogic();
        game.update();
        List<Word> answer = game.getAnswer();
        Word hint = game.getHint();
        List<String> english = game.getShuffledEnglishWords();
        assertTrue(answer.contains(hint));
        assertFalse(game.checkAnswer(english));
        assertTrue(game.checkAnswer(answer.stream().map(Word::getEnglish).collect(Collectors.toList())));
    }

    @Test
    public void wordPuzzleTest() {
        WordPuzzleLogic game = new WordPuzzleLogic();
        game.update();
        Phrase answer = game.getAnswer();
        assertTrue(MainController.getGameController().getPhrasesController().getCurrentThemeList().contains(answer));
        List<String> shuffled = game.getShuffledAnswer();
        String hint = game.getHint(0);

        assertTrue(answer.getEnglish().startsWith(hint));
        assertFalse(game.checkAnswer(shuffled));
        assertTrue(game.checkAnswer(Arrays.asList(answer.getEnglish().split(" "))));
    }


    @Test
    public void synonymsTest() {
        SynonymsLogic game = new SynonymsLogic();
        if (!game.update()) {
            return;
        }
        List<String> possible = game.getPossibleAnswers();
        List<String> answer = game.getAnswer();
        String hint = game.getHint();
        Word word = game.getWordTask();

        assert possible != null;
        assertTrue(possible.contains(hint));
        assertFalse(answer.contains(hint));

        int previousTotal = MainController.getGameController().getWordFactory().getWordTotalNumber(word);
        int previousError = MainController.getGameController().getWordFactory().getWordErrorNumber(word);

        assertTrue(MainController.getGameController().getWordListController().getCurrentListWords().contains(word));
        assertFalse(game.checkAnswer(Collections.singletonList(hint)));
        assertEquals(previousTotal + 1, MainController.getGameController().getWordFactory().getWordTotalNumber(word));
        assertEquals(previousError + 1, MainController.getGameController().getWordFactory().getWordErrorNumber(word));
        assertTrue(game.checkAnswer(answer));
        assertEquals(previousTotal + 2, MainController.getGameController().getWordFactory().getWordTotalNumber(word));
        assertEquals(previousError + 1, MainController.getGameController().getWordFactory().getWordErrorNumber(word));

    }
}
