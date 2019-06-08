package ru.hse.android.project.easyenglish.games;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ru.hse.android.project.easyenglish.GameActivity;
import ru.hse.android.project.easyenglish.R;
import ru.hse.android.project.easyenglish.ShowInfoActivity;
import ru.hse.android.project.easyenglish.controllers.MainController;
import ru.hse.android.project.easyenglish.words.Word;

/**
 * Local game to memorize English words and their spelling.
 * Rules : You are given word in English with shuffled letters. Your task is to put letters in right order and write down the result.
 */
public class LetterPuzzleActivity extends AppCompatActivity {

    /**
     * Generate word with shuffled letters from given until they are not equals.
     * @param word to shuffle letters
     * @return word with shuffled letters
     */
    private String shuffleLetters(String word) {
        List<String> letters = Arrays.asList(word.split(""));
        String shuffledWordResult = word;
        while (shuffledWordResult.equals(word) && word.length() > 1) {
            StringBuilder shuffledWord = new StringBuilder();
            Collections.shuffle(letters);
            for (String letter : letters) {
                shuffledWord.append(letter);
            }
            shuffledWordResult = shuffledWord.toString();
        }
        return shuffledWordResult;
    }

    /** Create game screen with English word task with shuffled letters. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letter_puzzle);

        final Word word = MainController.getGameController().getWordStorage().nextWord();
        final String shuffledEnglish = shuffleLetters(word.getEnglish());

        final TextView shuffledWordText = findViewById(R.id.word_task_text);
        shuffledWordText.setText((shuffledEnglish + " - " + word.getRussian()));

        final EditText answerText = findViewById(R.id.answer_text);

        Button checkAnswerButton = findViewById(R.id.send_answer_button);
        checkAnswerButton.setOnClickListener(v -> checkAnswer(answerText.getText().toString(), word.getEnglish(), word));

        Button showRulesButton = findViewById(R.id.rules_button);
        showRulesButton.setOnClickListener(v -> {
            ShowInfoActivity rules = new ShowInfoActivity();
            Bundle args = new Bundle();
            args.putString("title", getString(R.string.rules_letter_puzzle));
            args.putString("message", getString(R.string.rules_letter_puzzle_text));
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

        Button showHintsButton = findViewById(R.id.hints_button);
        showHintsButton.setOnClickListener(v -> {
            ShowInfoActivity rules = new ShowInfoActivity();
            Bundle args = new Bundle();
            args.putString("title", "Letter puzzle");
            args.putString("message", "The first letter is " + word.getEnglish().charAt(0) + ".");
            rules.setArguments(args);
            rules.show(getSupportFragmentManager(), "message");
        });
    }

    /** Check if given answer equals to model and send report to GameActivity. */
    private void checkAnswer(String givenAnswer, String modelAnswer, Word answer) {
        boolean result = givenAnswer.equals(modelAnswer);
        MainController.getGameController().saveWordResult(answer, result);
        Intent intent = new Intent();
        intent.putExtra("game result", result);
        intent.putExtra("word", answer.getRussian() + "\n" + answer.getEnglish() + "\n" + answer.getTranscription());
        setResult(RESULT_OK, intent);
        finish();
    }

    /** On back button pressed ask player if he want to end the game. */
    @Override
    public void onBackPressed() {
        GameActivity.onBackPressed(this);
    }
}
