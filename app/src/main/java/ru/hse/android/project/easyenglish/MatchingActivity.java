package ru.hse.android.project.easyenglish;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import ru.hse.android.project.easyenglish.adapters.DragAndDropAdapter;
import ru.hse.android.project.easyenglish.controllers.MainController;
import ru.hse.android.project.easyenglish.controllers.WordStorage;
import ru.hse.android.project.easyenglish.words.Word;

/**
 * Local game to memorize English words and their definitions.
 * Rules : You are given words in Russian and their shuffles English translations. Your task match English words with their definitions.
 */
public class MatchingActivity extends AppCompatActivity {

    /** Max number of possible answers(translations) for English task word. */
    private static final int SIZE = 4;

    private final Random random = new Random();

    /**
     * Generate shuffled word list from given until lists are not equals.
     * @param words list to shuffle
     * @return shuffled word list
     */
    private List<String> shuffleWords(List<String> words) {
        final List<String> shuffledWords = new ArrayList<>(words);
        while (words.equals(shuffledWords)) {
            Collections.shuffle(shuffledWords);
        }
        return shuffledWords;
    }

    /** Create game screen with list of Russian words and list with shuffled English translations. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching);

        final WordStorage wordStorage = MainController.getGameController().getWordStorage();
        final List<Word> words = wordStorage.getSetOfWords(SIZE);
        final List<String> englishWords = new ArrayList<>();
        final List<String> russianWords = new ArrayList<>();
        int size = words.size();

        for (Word word : words) {
            englishWords.add(word.getEnglish());
            russianWords.add(word.getRussian());
        }

        final List<String> shuffledEnglishWords = shuffleWords(englishWords);

        ListView listView = findViewById(R.id.matching_list);
        DragAndDropAdapter adapter = new DragAndDropAdapter(this, russianWords, R.layout.matching_item);
        listView.setAdapter(adapter);

        DragAndDropListView dragListView = findViewById(R.id.matching_drag_and_drop_list);
        DragAndDropAdapter dragListAdapter = new DragAndDropAdapter(this, shuffledEnglishWords,  R.layout.matching_item);
        dragListView.setAdapter(dragListAdapter);

        Button checkAnswerButton = findViewById(R.id.send_answer_button);
        checkAnswerButton.setOnClickListener(v -> checkAnswer(shuffledEnglishWords, englishWords, words));

        int wrongAnswerNumber = random.nextInt(size);

        Button showHintsButton = findViewById(R.id.hints_button);
        showHintsButton.setOnClickListener(v -> {
            ShowInfoActivity rules = new ShowInfoActivity();
            Bundle args = new Bundle();
            args.putString("title", "Matching");
            args.putString("message", englishWords.get(wrongAnswerNumber) + " - " + russianWords.get(wrongAnswerNumber));
            rules.setArguments(args);
            rules.show(getSupportFragmentManager(), "message");
        });

        Button showRulesButton = findViewById(R.id.rules_button);
        showRulesButton.setOnClickListener(v -> {
            ShowInfoActivity rules = new ShowInfoActivity();
            Bundle args = new Bundle();
            args.putString("title", getString(R.string.rules_matching));
            args.putString("message", getString(R.string.rules_matching_text));
            rules.setArguments(args);
            rules.show(getSupportFragmentManager(), "message");
        });

        Button endGameButton = findViewById(R.id.end_game_button);
        endGameButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("end game", true);
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    /** Check if given answer equals to model and send report to GameActivity. */
    private void checkAnswer(List<String> givenAnswer, List<String> modelAnswer, List<Word> answer) {
        boolean result = givenAnswer.equals(modelAnswer);
        Intent intent = new Intent();
        intent.putExtra("game result", result);
        StringBuilder answerText = new StringBuilder();
        for (Word word : answer) {
            answerText.append(word.getRussian()).append(" - ").append(word.getEnglish()).append("\n");
        }
        intent.putExtra("word", answer.toString());
        setResult(RESULT_OK, intent);
        finish();
    }

    /** On back button pressed ask player if he want to end the game. */
    @Override
    public void onBackPressed() {
        GameActivity.onBackPressed(this);
    }
}
