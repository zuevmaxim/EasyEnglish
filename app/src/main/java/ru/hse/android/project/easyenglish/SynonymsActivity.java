package ru.hse.android.project.easyenglish;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import ru.hse.android.project.easyenglish.controllers.MainController;
import ru.hse.android.project.easyenglish.controllers.TranslateController;
import ru.hse.android.project.easyenglish.controllers.WordStorage;
import ru.hse.android.project.easyenglish.words.Word;

/**
 * Local game to memorize English words and their synonyms.
 * Rules : You are given words in English. Your task is to choose all the synonyms of the word from the list.
 */
public class SynonymsActivity extends AppCompatActivity {

    /** Max number of possible answers(synonyms) for English task word. */
    private static final int SIZE = 5;

    private final Random random = new Random();

    /** Create game screen with with English word task and list of possible synonyms. */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_synonims);

        final WordStorage wordStorage = MainController.getGameController().getWordStorage();
        final List<Word> words = wordStorage.getSetOfWords(SIZE);

        Word wordTask = words.remove(0);
        final TextView wordTaskText = findViewById(R.id.word_task_text);
        wordTaskText.setText(wordTask.getEnglish());

        final List<String> synonyms = TranslateController.getSynonyms(wordTask.getEnglish());
        final List<String> notSynonyms = new ArrayList<>();

        if (synonyms == null) {
            Intent intent = new Intent();
            setResult(GameActivity.RESULT_REMOVE_SYNONYMS, intent);
            finish();
            return;
        }

        for (Word word : words) {
            if (!synonyms.contains(word.getEnglish())) {
                notSynonyms.add(word.getEnglish());
            }
        }

        final List<String> boxedWords = new ArrayList<>();
        final List<String> boxedSynonyms = new ArrayList<>();

        int synonymsCounter = random.nextInt(3) + 1;
        while (synonyms.size() > 0 && synonymsCounter > 0) {
            int nextSynonym = random.nextInt(synonyms.size());
            String synonym = synonyms.remove(nextSynonym);
            boxedSynonyms.add(synonym);
            boxedWords.add(synonym);
            synonymsCounter--;
        }

        int notSynonymsCounter = SIZE - boxedWords.size();
        while (notSynonyms.size() > 0 && notSynonymsCounter > 0) {
            int nextNotSynonym = random.nextInt(notSynonyms.size());
            boxedWords.add(notSynonyms.remove(nextNotSynonym));
            notSynonymsCounter--;
        }

        int size = boxedWords.size();
        String wrongAnswer = boxedWords.get(size - 1);
        Collections.shuffle(boxedWords);

        boxedSynonyms.forEach(System.out::println);

        LinearLayout checkBoxesLayout = findViewById(R.id.check_boxes_layout);
        final CheckBox[] checkBoxes = new CheckBox[size];
        for (int i = 0; i < size; i++) {
            checkBoxes[i]  = new CheckBox(this);
            checkBoxes[i].setText(boxedWords.get(i));
            checkBoxes[i].setTextColor(Color.parseColor("#CB000000"));
            checkBoxesLayout.addView(checkBoxes[i]);
        }

        Button checkAnswerButton = findViewById(R.id.send_answer_button);
        checkAnswerButton.setOnClickListener(v -> {
            List<String> checkedSynonyms = Arrays.stream(checkBoxes).filter(CompoundButton::isChecked).map(checkBox -> checkBox.getText().toString()).collect(Collectors.toList());
            checkAnswer(checkedSynonyms, boxedSynonyms, wordTask);
        });

        Button rulesButton = findViewById(R.id.rules_button);
        rulesButton.setOnClickListener(v -> {
            ShowInfoActivity rules = new ShowInfoActivity();
            Bundle args = new Bundle();
            args.putString("title", this.getString(R.string.rules_synonyms));
            args.putString("message", this.getString(R.string.rules_synonyms_text));
            rules.setArguments(args);
            rules.show(getSupportFragmentManager(), "message");
        });

        Button hintsButton = findViewById(R.id.hints_button);
        hintsButton.setOnClickListener(v -> {
            ShowInfoActivity hints = new ShowInfoActivity();
            Bundle args = new Bundle();
            args.putString("title", this.getString(R.string.synonyms));
            args.putString("message", wrongAnswer + " " + this.getString(R.string.is_wrong_answer));
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

    /** Check if given answer equals to model and send report to GameActivity. */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void checkAnswer(List<String> givenAnswer, List<String> modelAnswer, Word wordTask) {
        boolean result = givenAnswer.size() == modelAnswer.size();
        givenAnswer.forEach(System.out::println);
        modelAnswer.forEach(System.out::println);
        for (String word : givenAnswer) {
            result &= modelAnswer.contains(word);
        }
        MainController.getGameController().saveWordResult(wordTask, result);
        Intent intent = new Intent();
        intent.putExtra("game result", result);
        String answer;
        if (modelAnswer.size() == 0) {
            answer = "There was no synonyms for word " + wordTask.getEnglish() + ".";
        } else {
            answer = wordTask.getEnglish() + " - " + String.join(", ", modelAnswer);
        }
        intent.putExtra("word", answer);
        setResult(RESULT_OK, intent);
        finish();
    }

    /** On back button pressed ask player if he want to end the game. */
    @Override
    public void onBackPressed() {
        GameActivity.onBackPressed(this);
    }
}
