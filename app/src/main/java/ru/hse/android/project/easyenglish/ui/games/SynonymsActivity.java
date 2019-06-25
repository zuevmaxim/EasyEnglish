package ru.hse.android.project.easyenglish.ui.games;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import ru.hse.android.project.easyenglish.R;
import ru.hse.android.project.easyenglish.ui.GameActivity;
import ru.hse.android.project.easyenglish.ui.views.ShowInfoActivity;
import ru.hse.android.project.easyenglish.controllers.MainController;
import ru.hse.android.project.easyenglish.logic.SynonymsLogic;
import ru.hse.android.project.easyenglish.words.Word;

/**
 * Local game to memorize English words and their synonyms.
 * Rules : You are given words in English. Your task is to choose all the synonyms of the word from the list.
 */
public class SynonymsActivity extends AppCompatActivity {

    /** Tag for window with hints. */
    private static final String HINTS = "hints";

    /** Tag for window with rules. */
    private static final String RULES = "rules";

    private final SynonymsLogic logic = new SynonymsLogic();

    private Word wordTask;

    /** Create game screen with with English word task and list of possible synonyms. */
    @RequiresApi(api = Build.VERSION_CODES.O)
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
            checkBoxes[i].setTextColor(Color.parseColor("#CB000000")); //TODO
            checkBoxesLayout.addView(checkBoxes[i]);
        }

        Button checkAnswerButton = findViewById(R.id.send_answer_button);
        checkAnswerButton.setOnClickListener(v -> {
            List<String> checkedSynonyms = Arrays.stream(checkBoxes).filter(CompoundButton::isChecked).map(checkBox -> checkBox.getText().toString()).collect(Collectors.toList());
            checkAnswer(checkedSynonyms);
        });

        Button rulesButton = findViewById(R.id.rules_button);
        rulesButton.setOnClickListener(v -> {
            ShowInfoActivity rules = new ShowInfoActivity();
            Bundle args = new Bundle();
            args.putString(ShowInfoActivity.TITLE_TAG, this.getString(R.string.rules_synonyms));
            args.putString(ShowInfoActivity.MESSAGE_TAG, this.getString(R.string.rules_synonyms_text));
            rules.setArguments(args);
            rules.show(getSupportFragmentManager(), RULES);
        });

        Button hintsButton = findViewById(R.id.hints_button);
        hintsButton.setOnClickListener(v -> {
            ShowInfoActivity hints = new ShowInfoActivity();
            Bundle args = new Bundle();
            args.putString(ShowInfoActivity.TITLE_TAG, this.getString(R.string.synonyms));
            args.putString(ShowInfoActivity.MESSAGE_TAG, logic.getHint() + " " + this.getString(R.string.is_wrong_answer));
            hints.setArguments(args);
            hints.show(getSupportFragmentManager(), HINTS);
        });

        Button endGameButton = findViewById(R.id.end_game_button);
        endGameButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra(GameActivity.END_GAME_TAG, true);
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    /** Check if given answer equals to model and send report to GameActivity. */
    @RequiresApi(api = Build.VERSION_CODES.O)
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
            answerText = wordTask.getEnglish() + " - " + String.join(", ", answer);
        }
        intent.putExtra(GameActivity.WORD_TAG, answerText);
        setResult(RESULT_OK, intent);
        finish();
    }

    /** On back button pressed ask player if he want to end the game. */
    @Override
    public void onBackPressed() {
        GameActivity.onBackPressed(this);
    }
}
