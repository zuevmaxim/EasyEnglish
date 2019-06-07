package ru.hse.android.project.easyenglish;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ru.hse.android.project.easyenglish.adapters.DragAndDropAdapter;
import ru.hse.android.project.easyenglish.controllers.MainController;
import ru.hse.android.project.easyenglish.words.Phrase;
import ru.hse.android.project.easyenglish.controllers.PhraseStorage;

public class WordPuzzleActivity extends AppCompatActivity {

    private boolean result;

    private List<String> shuffleWords(List<String> words) {
        List<String> shuffledWordResult = new ArrayList<>(words);
        while (words.equals(shuffledWordResult) && shuffledWordResult.size() > 1) {
            Collections.shuffle(shuffledWordResult);
        }
        return shuffledWordResult;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_puzzle);

        PhraseStorage storage = MainController.getGameController().getPhraseStorage();
        Phrase phrase = storage.nextPhrase();

        TextView russianPhraseText = findViewById(R.id.russian_phrase_text);
        russianPhraseText.setText(phrase.getRussian());

        List<String> words = Arrays.asList(phrase.getEnglish().split(" "));
        List<String> shuffleWords = shuffleWords(words);
        DragAndDropListView dragListView = findViewById(R.id.drag_and_drop_list);
        DragAndDropAdapter dragListAdapter = new DragAndDropAdapter(this, shuffleWords, R.layout.word_puzzle_item);
        dragListView.setAdapter(dragListAdapter);

        Button checkAnswerButton = findViewById(R.id.send_answer_button);
        checkAnswerButton.setOnClickListener(v -> {
            v.setEnabled(false);
            result = words.equals(shuffleWords);
            Intent intent = new Intent();
            intent.putExtra("game result", result);
            intent.putExtra("word", phrase.getEnglish() + "\n" + phrase.getRussian());
            setResult(RESULT_OK, intent);
            finish();
        });

        Button showHintsButton = findViewById(R.id.hints_button);
        showHintsButton.setOnClickListener(v -> {
            ShowInfoActivity rules = new ShowInfoActivity();
            Bundle args = new Bundle();
            args.putString("title", "Word puzzle");
            if (words.size() >= 2) {
                args.putString("message", "The second word is " + words.get(1));
            } else if (words.size() >= 1) {
                args.putString("message", "The first word is " + words.get(0));
            }
            rules.setArguments(args);
            rules.show(getSupportFragmentManager(), "message");
        });

        Button showRulesButton = findViewById(R.id.rules_button);
        showRulesButton.setOnClickListener(v -> {
            ShowInfoActivity rules = new ShowInfoActivity();
            Bundle args = new Bundle();
            args.putString("title", getString(R.string.rules_word_puzzle));
            args.putString("message", getString(R.string.rules_word_puzzle_text));
            rules.setArguments(args);
            rules.show(getSupportFragmentManager(), "message");
        });

        Button endGameButton = findViewById(R.id.end_game_button);
        endGameButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("end game", true);
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        GameActivity.onBackPressed(this);
    }
}
