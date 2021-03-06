package ru.hse.android.project.easyenglish.ui.games;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.List;

import ru.hse.android.project.easyenglish.R;
import ru.hse.android.project.easyenglish.logic.ChooseDefinitionLogic;
import ru.hse.android.project.easyenglish.ui.GameActivity;
import ru.hse.android.project.easyenglish.words.Word;

/**
 * Activity for ChooseDefinition game with logic described in ChooseDefinitionLogic.
 */
public class ChooseDefinitionActivity extends AppCompatActivity {

    private final ChooseDefinitionLogic logic = new ChooseDefinitionLogic();

    /** Create game screen with English word task and group of possible Russian translations. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_definition);
        logic.update();

        List<Word> possibleAnswers = logic.getPossibleAnswers();
        Word answer = logic.getAnswer();

        final TextView taskWordText = findViewById(R.id.word_task_text);
        taskWordText.setText(String.format("%s\t\t%s", answer.getEnglish(), answer.getTranscription()));

        RadioGroup radioGroup = findViewById(R.id.answers_radio_group);
        final RadioButton[] radioButtons = new RadioButton[possibleAnswers.size()];
        for (int i = 0; i < possibleAnswers.size(); i++) {
            radioButtons[i]  = new RadioButton(this);
            radioButtons[i].setText(possibleAnswers.get(i).getRussian());
            radioButtons[i].setTextSize(18);
            radioButtons[i].setId(i);
            radioGroup.addView(radioButtons[i]);
        }

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> checkAnswer(possibleAnswers.get(checkedId), answer));

        GameActivity.initRulesButton(this,
                getString(R.string.rules_choose_definitions),
                getString(R.string.rules_text_choose_definitions));

        GameActivity.initHintsButton(this,
                getString(R.string.hints_choose_definitions),
                logic.getHint().getRussian() + " " + getString(R.string.is_wrong_answer));

        GameActivity.initEndGameButton(this);
    }

    /** Check answer and send report to GameActivity. */
    private void checkAnswer(@NonNull Word givenAnswer, @NonNull Word answer) {
        boolean result = logic.checkAnswer(givenAnswer);
        Intent intent = new Intent();
        intent.putExtra(GameActivity.GAME_RESULT_TAG, result);
        intent.putExtra(GameActivity.MESSAGE_TAG, answer.getRussian() + "\n"
                + answer.getEnglish() + "\n"
                + answer.getTranscription());
        setResult(RESULT_OK, intent);
        finish();
    }

    /** On back button pressed ask player if he want to end the game. */
    @Override
    public void onBackPressed() {
        GameActivity.onBackPressed(this);
    }
}
