package ru.hse.android.project.easyenglish;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import ru.hse.android.project.easyenglish.controllers.MainController;
import ru.hse.android.project.easyenglish.controllers.WordStorage;
import ru.hse.android.project.easyenglish.words.Word;

public class ChooseDefinitionActivity extends AppCompatActivity {
    private final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_definition);

        final WordStorage wordStorage = MainController.getGameController().getWordStorage();
        final List<Word> words = wordStorage.getSetOfWords(4);
        Word min = words.get(0);
        Collections.shuffle(words);

        int size = words.size();

        RadioGroup radioGroup = findViewById(R.id.answers_radio_group);
        final RadioButton[] radioButtons = new RadioButton[size];

        for (int i = 0; i < size; i++) {
            radioButtons[i]  = new RadioButton(this);
            radioButtons[i].setText(words.get(i).getRussian());
            radioButtons[i].setTextSize(24);
            radioButtons[i].setId(i);
            radioGroup.addView(radioButtons[i]);
        }

        final TextView taskWordText = findViewById(R.id.word_task_text);

        final int answerNumber = words.indexOf(min);
        final Word answer = words.get(answerNumber);
        final int wrongAnswerNumber = setHint(size, answerNumber);

        taskWordText.setText(String.format("%s%s", answer.getEnglish(), answer.getTranscription()));

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case 0:
                    checkAnswer(0, answerNumber, answer);
                    break;
                case 1:
                    checkAnswer(1, answerNumber, answer);
                    break;
                case 2:
                    checkAnswer(2, answerNumber, answer);
                    break;
                case 3:
                    checkAnswer(3, answerNumber, answer);
                    break;
            }
        });

        Button rulesButton = findViewById(R.id.rules_button);
        rulesButton.setOnClickListener(v -> {
            ShowInfoActivity rules = new ShowInfoActivity();
            Bundle args = new Bundle();
            args.putString("game", "Choose definition rules:");
            args.putString("message", "You are given a word in English. Your task is to choose right Russian definition for it.");
            rules.setArguments(args);
            rules.show(getSupportFragmentManager(), "rules");
        });

        Button hintsButton = findViewById(R.id.hints_button);
        hintsButton.setOnClickListener(v -> {
            ShowInfoActivity hints = new ShowInfoActivity();
            Bundle args = new Bundle();
            args.putString("title", "Choose definition hints:");
            args.putString("message", words.get(wrongAnswerNumber).getRussian() + " is a wrong answer");
            hints.setArguments(args);
            hints.show(getSupportFragmentManager(), "hints");
        });

        Button endGameButton = findViewById(R.id.end_game_button);
        endGameButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("end game", true);
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    private int setHint(int size, int answerNumber) {
        int newWrongAnswerNumber = random.nextInt(size);
        while (newWrongAnswerNumber == answerNumber) {
            newWrongAnswerNumber = random.nextInt(size);
        }
        return newWrongAnswerNumber;
    }

    private void checkAnswer(int givenAnswer, int modelAnswer, Word answer) {
        boolean result = (givenAnswer == modelAnswer);
        MainController.getGameController().saveWordResult(answer, result);
        Intent intent = new Intent();
        intent.putExtra("game result", result);
        intent.putExtra("word", answer.getRussian() + "-" + answer.getEnglish() + " " + answer.getTranscription());
        setResult(RESULT_OK, intent);
        finish();
    }
}
