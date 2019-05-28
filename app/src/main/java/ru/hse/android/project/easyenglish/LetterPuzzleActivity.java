package ru.hse.android.project.easyenglish;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ru.hse.android.project.easyenglish.controllers.MainController;
import ru.hse.android.project.easyenglish.words.Word;

public class LetterPuzzleActivity extends AppCompatActivity {

    private boolean result;

    private String shuffleLetters(String word) {
        List<String> letters = Arrays.asList(word.split(""));
        String shuffledWordResult = word;
        while (shuffledWordResult.equals(word) && word.length() > 0) {
            StringBuilder shuffledWord = new StringBuilder();
            Collections.shuffle(letters);
            for (String letter : letters) {
                shuffledWord.append(letter);
            }
            shuffledWordResult = shuffledWord.toString();
        }
        return shuffledWordResult;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letter_puzzle);

        final Word word = MainController.getGameController().getWordStorage().nextWord();
        final String english = word.getEnglish();
        final String russian = word.getRussian();
        final String shuffledEnglish = shuffleLetters(english);

        final TextView shuffledWordText = findViewById(R.id.word_task_text);
        shuffledWordText.setText((shuffledEnglish + " - " + russian));

        Button checkAnswerButton = findViewById(R.id.check_answer_button);
        checkAnswerButton.setOnClickListener(v -> {
            final EditText answerText = findViewById(R.id.answer_text);
            String answer = answerText.getText().toString();
            v.setEnabled(false);
            result = answer.equals(english);
            MainController.getGameController().saveWordResult(word, result);
            Intent intent = new Intent();
            intent.putExtra("game result", result);
            intent.putExtra("word", word.getRussian() + "\n" + word.getEnglish() + "\n" + word.getTranscription());
            setResult(RESULT_OK, intent);
            finish();
        });

        Button showRulesButton = findViewById(R.id.rules_button);
        showRulesButton.setOnClickListener(v -> {
            ShowInfoActivity rules = new ShowInfoActivity();
            Bundle args = new Bundle();
            args.putString("game", this.getString(R.string.rules_letter_puzzle));
            args.putString("rule", this.getString(R.string.rules_letter_puzzle_text));
            rules.setArguments(args);
            rules.show(getSupportFragmentManager(), "rule");
        });

        Button endGameButton = findViewById(R.id.end_game_button);
        endGameButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("end game", true);
            setResult(RESULT_OK, intent);
            finish();
        });
    }
}
