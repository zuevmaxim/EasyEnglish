package ru.hse.android.easyenglish;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

        Button checkAnswerButton = findViewById(R.id.check_answer_letter_puzzle_button);
        Button endGameButton = findViewById(R.id.end_game_letter_puzzle_button);
        Button showRulesButton = findViewById(R.id.show_rules_letter_puzzle_button);

        final String translation = MainController.getGameController().getWordStorage().nextWord();
        final String word = TranslateController.translate(translation, "ru-en");
        final String shuffledWord = shuffleLetters(word);

        final TextView shuffledWordText = findViewById(R.id.shuffled_word_text);
        shuffledWordText.setText((shuffledWord + " - " + translation));

        checkAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText answerText = findViewById(R.id.answer_letter_puzzle_text);
                String answer = answerText.getText().toString();
                v.setEnabled(false);
                result = answer.equals(word);
                MainController.getGameController().saveWordResult(translation, result);
                Intent intent = new Intent();
                intent.putExtra("game result", result);
                setResult(RESULT_OK, intent);
                finish();
            }
        });


        showRulesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowInfoActivity rules = new ShowInfoActivity();
                Bundle args = new Bundle();
                args.putString("game", "Letter puzzle");
                args.putString("rule", "You are given a word in English with shuffled letters and its translation. Your task is to put letters in right order and write down the result.");
                rules.setArguments(args);
                rules.show(getSupportFragmentManager(), "rule");
            }
        });

        endGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("end game", true);
                setResult(RESULT_OK, intent);
                finish();
            }
        });


    }
}
