package ru.hse.android.easyenglish;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class ChooseDefinitionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_definition);

        RadioGroup answersRadioGroup = findViewById(R.id.answers_radio_group);
        Button endGameButton = findViewById(R.id.end_choose_definition_game_button);

        final RadioButton answerRadioButton0 = findViewById(R.id.answer_radio_button_0);
        final RadioButton answerRadioButton1 = findViewById(R.id.answer_radio_button_1);
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
}
