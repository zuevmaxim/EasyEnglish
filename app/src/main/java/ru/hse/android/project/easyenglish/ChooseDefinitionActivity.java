package ru.hse.android.project.easyenglish;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import ru.hse.android.project.easyenglish.controllers.MainController;
import ru.hse.android.project.easyenglish.controllers.WordStorage;
import ru.hse.android.project.easyenglish.words.Word;

/**
 * Local game to memorize English words and their translation.
 * Rules : You are given a word in English. Your task is to choose right Russian definition for it.
 */
public class ChooseDefinitionActivity extends AppCompatActivity {

    /** Max number of possible answers(translations) for English task word. */
    private final static int ANSWERS_SIZE = 4;

    private final Random random = new Random();

    /** Create game screen with English word task and group of possible Russian translations. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_definition);

        final WordStorage wordStorage = MainController.getGameController().getWordStorage();
        final List<Word> words = wordStorage.getSetOfWords(ANSWERS_SIZE);
        Word answer = words.get(0);
        Collections.shuffle(words);
        int size = words.size();

        final int answerNumber = words.indexOf(answer);
        final int wrongAnswerNumber = setHint(size, answerNumber);

        final TextView taskWordText = findViewById(R.id.word_task_text);
        taskWordText.setText(String.format("%s\t\t%s", answer.getEnglish(), answer.getTranscription()));

        RadioGroup radioGroup = findViewById(R.id.answers_radio_group);
        final RadioButton[] radioButtons = new RadioButton[size];

        for (int i = 0; i < size; i++) {
            radioButtons[i]  = new RadioButton(this);
            radioButtons[i].setText(words.get(i).getRussian());
            radioButtons[i].setTextSize(18);
            radioButtons[i].setId(i);
            radioGroup.addView(radioButtons[i]);
        }

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> checkAnswer(checkedId, answerNumber, answer));

        Button rulesButton = findViewById(R.id.rules_button);
        rulesButton.setOnClickListener(v -> {
            ShowInfoActivity rules = new ShowInfoActivity();
            Bundle args = new Bundle();
            args.putString("title", this.getString(R.string.rules_choose_definitions));
            args.putString("message", this.getString(R.string.rules_text_choose_definitions));
            rules.setArguments(args);
            rules.show(getSupportFragmentManager(), "message");
        });

        Button hintsButton = findViewById(R.id.hints_button);
        hintsButton.setOnClickListener(v -> {
            ShowInfoActivity hints = new ShowInfoActivity();
            Bundle args = new Bundle();
            args.putString("title", this.getString(R.string.hints_choose_definitions));
            args.putString("message", words.get(wrongAnswerNumber).getRussian() + " "
                    + this.getString(R.string.is_wrong_answer));
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

    /** Choose word for hint. */
    private int setHint(int size, int answerNumber) {
        int newWrongAnswerNumber = random.nextInt(size);
        while (newWrongAnswerNumber == answerNumber) {
            newWrongAnswerNumber = random.nextInt(size);
        }
        return newWrongAnswerNumber;
    }

    /** Check if given answer equals to model and send report to GameController. */
    private void checkAnswer(int givenAnswer, int modelAnswer, @NotNull Word answer) {
        boolean result = (givenAnswer == modelAnswer);
        MainController.getGameController().saveWordResult(answer, result);
        Intent intent = new Intent();
        intent.putExtra("game result", result);
        intent.putExtra("word", answer.getRussian()
                + "\n" + answer.getEnglish() + "\n"
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
