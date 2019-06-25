package ru.hse.android.project.easyenglish.ui.games;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import java.util.List;

import ru.hse.android.project.easyenglish.R;
import ru.hse.android.project.easyenglish.logic.WordPuzzleLogic;
import ru.hse.android.project.easyenglish.ui.GameActivity;
import ru.hse.android.project.easyenglish.ui.views.DragAndDropListView;
import ru.hse.android.project.easyenglish.ui.views.ShowInfoActivity;
import ru.hse.android.project.easyenglish.ui.views.adapters.DragAndDropAdapter;
import ru.hse.android.project.easyenglish.words.Phrase;

/**
 * Local game to memorize common English phrases.
 * Rules : You are given phrase in English with shuffled words. Your task is to put words in right order and write down the result.
 */
public class WordPuzzleActivity extends AppCompatActivity {

    private final WordPuzzleLogic logic = new WordPuzzleLogic();

    /** Create game screen with phrase with shuffled words. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_puzzle);

        logic.update();

        List<String> shuffleWords = logic.getShuffledAnswer();

        DragAndDropListView dragListView = findViewById(R.id.drag_and_drop_list);
        DragAndDropAdapter dragListAdapter = new DragAndDropAdapter(this, shuffleWords, R.layout.word_puzzle_item);
        dragListView.setAdapter(dragListAdapter);

        Button checkAnswerButton = findViewById(R.id.send_answer_button);
        checkAnswerButton.setOnClickListener(v -> checkAnswer(shuffleWords));

        Button showRulesButton = findViewById(R.id.rules_button);
        showRulesButton.setOnClickListener(v -> {
            ShowInfoActivity rules = new ShowInfoActivity();
            Bundle args = new Bundle();
            args.putString(ShowInfoActivity.TITLE_TAG, getString(R.string.rules_word_puzzle));
            args.putString(ShowInfoActivity.MESSAGE_TAG, getString(R.string.rules_word_puzzle_text));
            rules.setArguments(args);
            rules.show(getSupportFragmentManager(), GameActivity.RULES_TAG);
        });

        Button showHintsButton = findViewById(R.id.hints_button);
        showHintsButton.setOnClickListener(v -> {
            ShowInfoActivity rules = new ShowInfoActivity();
            Bundle args = new Bundle();
            args.putString(ShowInfoActivity.TITLE_TAG, getString(R.string.word_puzzle_hints));
            if (shuffleWords.size() >= 2) {
                args.putString(ShowInfoActivity.MESSAGE_TAG, getString(R.string.word_puzzle_first_word_hints) + logic.getHint(1));
            } else if (shuffleWords.size() >= 1) {
                args.putString(ShowInfoActivity.MESSAGE_TAG, getString(R.string.word_puzzle_second_word_hints) + logic.getHint(0));
            }
            rules.setArguments(args);
            rules.show(getSupportFragmentManager(), GameActivity.HINTS_TAG);
        });

        Button endGameButton = findViewById(R.id.end_game_button);
        endGameButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra(GameActivity.END_GAME_TAG, true);
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    /** Check if given answer equals to model and send report to GameActivity. */
    private void checkAnswer(@NonNull List<String> givenAnswer) {
        boolean result = logic.checkAnswer(givenAnswer);
        Phrase answer = logic.getAnswer();
        Intent intent = new Intent();
        intent.putExtra(GameActivity.GAME_RESULT_TAG, result);
        intent.putExtra(GameActivity.MESSAGE_TAG, answer.getEnglish() + "\n" + answer.getRussian());
        setResult(RESULT_OK, intent);
        finish();
    }

    /** On back button pressed ask player if he want to end the game. */
    @Override
    public void onBackPressed() {
        GameActivity.onBackPressed(this);
    }
}
