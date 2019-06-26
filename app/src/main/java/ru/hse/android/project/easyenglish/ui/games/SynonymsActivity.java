package ru.hse.android.project.easyenglish.ui.games;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import ru.hse.android.project.easyenglish.R;
import ru.hse.android.project.easyenglish.controllers.MainController;
import ru.hse.android.project.easyenglish.logic.SynonymsLogic;
import ru.hse.android.project.easyenglish.ui.GameActivity;
import ru.hse.android.project.easyenglish.words.Word;

/**
 * Local game to memorize English words and their synonyms.
 * Rules : You are given words in English. Your task is to choose all the synonyms of the word from the list.
 */
public class SynonymsActivity extends AppCompatActivity {

    private final SynonymsLogic logic = new SynonymsLogic();

    private Word wordTask;

    /** Create game screen with with English word task and list of possible synonyms. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_synonims);

        if (!logic.update()) {
            Intent intent = new Intent();
            setResult(GameActivity.RESULT_REMOVE_SYNONYMS, intent);
            finish();
            return;
        }

        wordTask = logic.getWordTask();
        final TextView wordTaskText = findViewById(R.id.word_task_text);
        wordTaskText.setText(wordTask.getEnglish());

        final List<String> possibleAnswers = logic.getPossibleAnswers();
        LinearLayout checkBoxesLayout = findViewById(R.id.check_boxes_layout);
        assert possibleAnswers != null;
        final CheckBox[] checkBoxes = new CheckBox[possibleAnswers.size()];
        for (int i = 0; i < possibleAnswers.size(); i++) {
            checkBoxes[i]  = new CheckBox(this);
            checkBoxes[i].setText(possibleAnswers.get(i));
            checkBoxes[i].setTextColor(ContextCompat.getColor(this, R.color.colorDark));
            checkBoxesLayout.addView(checkBoxes[i]);
        }

        Button checkAnswerButton = findViewById(R.id.send_answer_button);
        checkAnswerButton.setOnClickListener(v -> {
            List<String> checkedSynonyms = Arrays.stream(checkBoxes)
                    .filter(CompoundButton::isChecked)
                    .map(checkBox -> checkBox.getText().toString())
                    .collect(Collectors.toList());
            checkAnswer(checkedSynonyms);
        });

        GameActivity.initRulesButton(this,
                getString(R.string.rules_synonyms),
                getString(R.string.rules_synonyms_text));

        GameActivity.initHintsButton(this,
                getString(R.string.synonyms),
                logic.getHint() + " " + this.getString(R.string.is_wrong_answer));

        GameActivity.initEndGameButton(this);
    }

    /** Check if given answer equals to model and send report to GameActivity. */
    private void checkAnswer(@NonNull List<String> givenAnswer) {
        boolean result = logic.checkAnswer(givenAnswer);
        List<String> answer = logic.getAnswer();
        MainController.getGameController().saveWordResult(wordTask, result);
        Intent intent = new Intent();
        intent.putExtra(GameActivity.GAME_RESULT_TAG, result);
        String answerText;
        assert answer != null;
        if (answer.size() == 0) {
            answerText = getString(R.string.no_synonyms_text) + wordTask.getEnglish() + ".";
        } else {
            answerText = wordTask.getEnglish() + " - " + TextUtils.join(", ", answer);
        }
        intent.putExtra(GameActivity.MESSAGE_TAG, answerText);
        setResult(RESULT_OK, intent);
        finish();
    }

    /** On back button pressed ask player if he want to end the game. */
    @Override
    public void onBackPressed() {
        GameActivity.onBackPressed(this);
    }
}
