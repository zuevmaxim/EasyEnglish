package ru.hse.android.easyenglish;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ru.hse.android.easyenglish.controllers.MainController;
import ru.hse.android.easyenglish.words.Word;

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

        final Word word = MainController.getGameController().getWordStorage().nextWord();
        final String english = word.getEnglish();
        final String russian = word.getRussian();
        final String shuffledEnglish = shuffleLetters(english);

        final TextView shuffledWordText = findViewById(R.id.shuffled_word_text);
        shuffledWordText.setText((shuffledEnglish + " - " + russian));

        checkAnswerButton.setOnClickListener(v -> {
            final EditText answerText = findViewById(R.id.answer_letter_puzzle_text);
            String answer = answerText.getText().toString();
            v.setEnabled(false);
            result = answer.equals(english);
            MainController.getGameController().saveWordResult(word, result);
            Intent intent = new Intent();
            intent.putExtra("game result", result);
            intent.putExtra("word", word.getRussian() + "-" + word.getEnglish() + " " + word.getTranscription());
            setResult(RESULT_OK, intent);
            finish();
        });


        showRulesButton.setOnClickListener(v -> {
            ShowInfoActivity rules = new ShowInfoActivity();
            Bundle args = new Bundle();
            args.putString("game", "Letter puzzle");
            args.putString("rule", "You are given a word in English with shuffled letters and its translation. Your task is to put letters in right order and write down the result.");
            rules.setArguments(args);
            rules.show(getSupportFragmentManager(), "rule");
        });

        endGameButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("end game", true);
            setResult(RESULT_OK, intent);
            finish();
        });


    }
}
