package ru.hse.android.project.easyenglish.ui.games;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import ru.hse.android.project.easyenglish.R;
import ru.hse.android.project.easyenglish.ui.GameActivity;
import ru.hse.android.project.easyenglish.ui.views.ShowInfoActivity;
import ru.hse.android.project.easyenglish.logic.LetterPuzzleLogic;
import ru.hse.android.project.easyenglish.words.Word;

/**
 * Activity for LetterPuzzle game with logic described in LetterPuzzleLogic.
 */
public class LetterPuzzleActivity extends AppCompatActivity {

    private final LetterPuzzleLogic logic = new LetterPuzzleLogic();

    private Word answer;

    /** Create game screen with English word task with shuffled letters. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letter_puzzle);
        logic.update();
        answer = logic.getAnswer();

        final String shuffledAnswer = logic.getShuffledAnswer();

        final TextView shuffledWordText = findViewById(R.id.word_task_text);
        shuffledWordText.setText((shuffledAnswer + " - " + answer.getRussian()));

        final EditText answerText = findViewById(R.id.answer_text);

        Button checkAnswerButton = findViewById(R.id.send_answer_button);
        checkAnswerButton.setOnClickListener(v -> checkAnswer(answerText.getText().toString()));

        Button showRulesButton = findViewById(R.id.rules_button);
        showRulesButton.setOnClickListener(v -> {
            ShowInfoActivity rules = new ShowInfoActivity();
            Bundle args = new Bundle();
            args.putString(ShowInfoActivity.TITLE_TAG, getString(R.string.rules_letter_puzzle));
            args.putString(ShowInfoActivity.MESSAGE_TAG, getString(R.string.rules_letter_puzzle_text));
            rules.setArguments(args);
            rules.show(getSupportFragmentManager(), GameActivity.RULES_TAG);
        });

        Button endGameButton = findViewById(R.id.end_game_button);
        endGameButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra(GameActivity.END_GAME_TAG, true);
            setResult(RESULT_OK, intent);
            finish();
        });

        Button showHintsButton = findViewById(R.id.hints_button);
        showHintsButton.setOnClickListener(v -> {
            ShowInfoActivity rules = new ShowInfoActivity();
            Bundle args = new Bundle();
            args.putString(ShowInfoActivity.TITLE_TAG, getString(R.string.hints_letter_puzzle));
            args.putString(ShowInfoActivity.MESSAGE_TAG, getString(R.string.hints_letter_puzzle_text) + logic.getHint() + ".");
            rules.setArguments(args);
            rules.show(getSupportFragmentManager(), GameActivity.HINTS_TAG);
        });
    }

    /** Check given answer and send report to GameActivity. */
    private void checkAnswer(@NonNull String givenAnswer) {
        boolean result = logic.checkAnswer(givenAnswer);
        Intent intent = new Intent();
        intent.putExtra(GameActivity.GAME_RESULT_TAG, result);
        intent.putExtra(GameActivity.MESSAGE_TAG, answer.getRussian() + "\n" + answer.getEnglish() + "\n" + answer.getTranscription());
        setResult(RESULT_OK, intent);
        finish();
    }

    /** On back button pressed ask player if he want to end the game. */
    @Override
    public void onBackPressed() {
        GameActivity.onBackPressed(this);
    }
}
