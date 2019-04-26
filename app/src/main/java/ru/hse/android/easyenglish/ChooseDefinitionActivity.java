package ru.hse.android.easyenglish;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ChooseDefinitionActivity extends AppCompatActivity {
    private final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_definition);

        final RadioButton[] answerRadioButtons = new RadioButton[4];

        answerRadioButtons[0] = findViewById(R.id.answer_radio_button_0);
        answerRadioButtons[1] = findViewById(R.id.answer_radio_button_1);
        answerRadioButtons[2] = findViewById(R.id.answer_radio_button_2);
        answerRadioButtons[3] = findViewById(R.id.answer_radio_button_3);

        final TextView taskWordText = findViewById(R.id.word_task_text);

        final List<Word> answerList = new ArrayList<>();
        final WordStorage wordStorage = MainController.getGameController().getWordStorage();
        for (int i = 0; i < 4; i++) {
            answerList.add(wordStorage.nextWord());
        }
        Collections.shuffle(answerList);

        final int answerNumber = random.nextInt(4);
        int newWrongAnswerNumber = random.nextInt(4);
        while (newWrongAnswerNumber == answerNumber) {
            newWrongAnswerNumber = random.nextInt(4);
        }
        final int wrongAnswerNumber = newWrongAnswerNumber;

        final Word answer = answerList.get(answerNumber);
        for (int i = 0; i < 4; ++i) {
            answerRadioButtons[i].setText(answerList.get(i).getRussian());
        }
        taskWordText.setText(answer.getEnglish());

        RadioGroup answersRadioGroup = findViewById(R.id.answers_radio_group);
        answersRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.answer_radio_button_0:
                    checkAnswer(0, answerNumber, answer);
                    break;
                case R.id.answer_radio_button_1:
                    checkAnswer(1, answerNumber, answer);
                    break;
                case R.id.answer_radio_button_2:
                    checkAnswer(2, answerNumber, answer);
                    break;
                case R.id.answer_radio_button_3:
                    checkAnswer(3, answerNumber, answer);
                    break;
            }
        });

        Button showRulesButton = findViewById(R.id.show_rules_choose_definition_button);
        showRulesButton.setOnClickListener(v -> {
            ShowInfoActivity rules = new ShowInfoActivity();
            Bundle args = new Bundle();
            args.putString("game", "Choose definition rules:");
            args.putString("message", "You are given a word in English. Your task is to choose right Russian definition for it.");
            rules.setArguments(args);
            rules.show(getSupportFragmentManager(), "rules");
        });

        Button showHintsButton = findViewById(R.id.show_hints_choose_definition_button);
        showHintsButton.setOnClickListener(v -> {
            ShowInfoActivity hints = new ShowInfoActivity();
            Bundle args = new Bundle();
            args.putString("title", "Choose definition hints:");
            args.putString("message", answerList.get(wrongAnswerNumber) + " is a wrong answer");
            hints.setArguments(args);
            hints.show(getSupportFragmentManager(), "hints");
        });

        Button endGameButton = findViewById(R.id.end_choose_definition_game_button);
        endGameButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("end game", true);
            setResult(RESULT_OK, intent);
            finish();
        });
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
