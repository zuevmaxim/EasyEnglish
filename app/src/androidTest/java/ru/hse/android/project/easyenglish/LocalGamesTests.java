package ru.hse.android.project.easyenglish;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ru.hse.android.project.easyenglish.controllers.MainController;
import ru.hse.android.project.easyenglish.exceptions.WrongListNameException;
import ru.hse.android.project.easyenglish.exceptions.WrongWordException;
import ru.hse.android.project.easyenglish.games.logic.ChooseDefinitionLogic;
import ru.hse.android.project.easyenglish.words.Word;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class LocalGamesTests {

    private List<Word> testList;

    @Before
    public void initMainController() throws WrongListNameException, WrongWordException {
        Context appContext = InstrumentationRegistry.getTargetContext();
        MainController.init(appContext);

        testList = new ArrayList<>(Arrays.asList(
                new Word ("первоеслово", "firstword"),
                new Word ("второеслово", "secondword"),
                new Word ("третьеслово", "thirdword"),
                new Word ("четвертоеслово", "fourthword"),
                new Word ("пятоеслово", "fifthword"),
                new Word ("шестоеслово", "sixthword")));
        MainController.getGameController().getWordListController().addNewWordList("LocalGameTest", testList);
    }

    @After
    public void deleteTestList() throws WrongListNameException {
        MainController.getGameController().getWordListController().deleteWordList("LocalGameTest");
    }

    @Test
    public void ChooseDefinitionTest() {
        MainController.getGameController().getWordListController().setCurrentWordList("LocalGameTest");
        ChooseDefinitionLogic game = new ChooseDefinitionLogic();
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
    public void LetterPuzzleTest() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        MainController.init(appContext);
        assertTrue(MainController.getGameController().getWordFactory().containsWord(new Word("корова" ,"cow")));

        assertEquals("ru.hse.android.project.easyenglish", appContext.getPackageName());
    }

    @Test
    public void MatchingTest() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        MainController.init(appContext);
        assertTrue(MainController.getGameController().getWordFactory().containsWord(new Word("корова" ,"cow")));

        assertEquals("ru.hse.android.project.easyenglish", appContext.getPackageName());
    }

    @Test
    public void WordPuzzleTest() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        MainController.init(appContext);
        assertTrue(MainController.getGameController().getWordFactory().containsWord(new Word("корова" ,"cow")));

        assertEquals("ru.hse.android.project.easyenglish", appContext.getPackageName());
    }


    @Test
    public void SynonymsTest() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        MainController.init(appContext);
        assertTrue(MainController.getGameController().getWordFactory().containsWord(new Word("корова" ,"cow")));

        assertEquals("ru.hse.android.project.easyenglish", appContext.getPackageName());
    }
}
