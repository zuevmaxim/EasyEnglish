package ru.hse.android.easyenglish;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class ChooseDefinitionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_definition);

        RadioGroup answersRadioGroup = findViewById(R.id.answers_radio_group);

        final RadioButton answerRadioButton0 = findViewById(R.id.answer_radio_button_0);
        final RadioButton answerRadioButton1 = findViewById(R.id.answer_radio_button_1);
        final RadioButton answerRadioButton2 = findViewById(R.id.answer_radio_button_2);
        final RadioButton answerRadioButton3 = findViewById(R.id.answer_radio_button_3);

        answersRadioGroup.clearCheck();

        answersRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.answer_radio_button_0:
                        mInfoTextView.setText("Подумайте ещё раз");
                        break;
                    case R.id.answer_radio_button_1:
                        mInfoTextView.setText("Подумайте ещё раз");
                        break;
                    case R.id.answer_radio_button_2:
                        mInfoTextView.setText("Подумайте ещё раз");
                        break;
                    case R.id.answer_radio_button_3:
                        mInfoTextView.setText("Подумайте ещё раз");
                        break;
                }
            }
        });
    }
}
