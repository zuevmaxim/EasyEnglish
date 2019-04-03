package ru.hse.android.easyenglish;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ChooseDefinitionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_definition);

        RadioGroup answersRadioGroup = findViewById(R.id.answers_radio_group);
        Button endGameButton = findViewById(R.id.end_choose_definition_game_button);

        final RadioButton answerRadioButton0 = findViewById(R.id.answer_radio_button_0);
        final RadioButton answerRadioButton1 = findViewById(R.id.answer_letter_puzzle_text);
        final RadioButton answerRadioButton2 = findViewById(R.id.answer_radio_button_2);
        final RadioButton answerRadioButton3 = findViewById(R.id.answer_radio_button_3);

        final TextView taskWordText = findViewById(R.id.word_task_text);

        List<String> answerList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            answerList.add(MainController.getGameController().getWordStorage().nextWord());
        }
        Collections.shuffle(answerList);

        final int answerNumber = (new Random()).nextInt(4);
        final String answer = answerList.get(answerNumber);
        answerRadioButton0.setText(answerList.get(0));
        answerRadioButton1.setText(answerList.get(1));
        answerRadioButton2.setText(answerList.get(2));
        answerRadioButton3.setText(answerList.get(3));
        taskWordText.setText(TranslateController.translate(answer, "ru-en"));

        answersRadioGroup.clearCheck();
        answersRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.answer_radio_button_0:
                        checkAnswer(0, answerNumber, answer);
                        break;
                    case R.id.answer_letter_puzzle_text:
                        checkAnswer(1, answerNumber, answer);
                        break;
                    case R.id.answer_radio_button_2:
                        checkAnswer(2, answerNumber, answer);
                        break;
                    case R.id.answer_radio_button_3:
                        checkAnswer(3, answerNumber, answer);
                        break;
                }
            }
        });

        Button showRulesButton = findViewById(R.id.show_rules_choose_definition_button);
        showRulesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RuleActivity rules = new RuleActivity();
                Bundle args = new Bundle();
                args.putString("game", "Choose definition");
                args.putString("rule", "You are given a word in English. Your task is to choose right Russian definition for it.");
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

    private void checkAnswer(int givenAnswer, int modelAnswer, String answer) {
        boolean result = (givenAnswer == modelAnswer);
        MainController.getGameController().saveWordResult(answer, result);
        Intent intent = new Intent();
        intent.putExtra("game result", result);
        setResult(RESULT_OK, intent);
        finish();
    }


    public static class GameRule extends AppCompatDialogFragment {
    }
}
