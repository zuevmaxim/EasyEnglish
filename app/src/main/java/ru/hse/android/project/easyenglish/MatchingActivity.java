package ru.hse.android.project.easyenglish;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import ru.hse.android.project.easyenglish.adapters.DragAndDropAdapter;
import ru.hse.android.project.easyenglish.controllers.MainController;
import ru.hse.android.project.easyenglish.controllers.WordStorage;
import ru.hse.android.project.easyenglish.words.Word;

public class MatchingActivity extends AppCompatActivity {

    private final Random random = new Random();
    private boolean result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching);


        final WordStorage wordStorage = MainController.getGameController().getWordStorage();
        final List<Word> words = wordStorage.getSetOfWords(4);
        final List<String> englishWords = new ArrayList<>();
        final List<String> russianWords = new ArrayList<>();
        int size = words.size();

        for (Word word : words) {
            englishWords.add(word.getEnglish());
            russianWords.add(word.getRussian());
        }

        final List<String> shufflesRussiaWords = new ArrayList<>(russianWords);
        Collections.shuffle(shufflesRussiaWords);

        ListView listView = findViewById(R.id.matching_list);
        DragAndDropAdapter adapter = new DragAndDropAdapter(this, englishWords);
        listView.setAdapter(adapter);

        DragAndDropListView dragListView = findViewById(R.id.matching_drag_and_drop_list);
        DragAndDropAdapter dragListAdapter = new DragAndDropAdapter(this, shufflesRussiaWords);
        dragListView.setAdapter(dragListAdapter);

        Button checkAnswerButton = findViewById(R.id.check_answer);
        checkAnswerButton.setOnClickListener(v -> {
            v.setEnabled(false);
            result = russianWords.equals(shufflesRussiaWords);
            Intent intent = new Intent();
            intent.putExtra("game result", result);
            StringBuilder answer = new StringBuilder();
            for (Word word : words) {
                answer.append(word.getEnglish()).append(" - ").append(word.getRussian()).append("\n");
            }
            intent.putExtra("word", answer.toString());
            setResult(RESULT_OK, intent);
            finish();
        });

        int wrongAnswerNumber = random.nextInt(size);

        Button showHintsButton = findViewById(R.id.show_hints);
        showHintsButton.setOnClickListener(v -> {
            ShowInfoActivity rules = new ShowInfoActivity();
            Bundle args = new Bundle();
            args.putString("title", "Word puzzle");
            args.putString("message", englishWords.get(wrongAnswerNumber) + " - " + russianWords.get(wrongAnswerNumber));
            rules.setArguments(args);
            rules.show(getSupportFragmentManager(), "message");
        });

        Button showRulesButton = findViewById(R.id.show_rules);
        showRulesButton.setOnClickListener(v -> {
            ShowInfoActivity rules = new ShowInfoActivity();
            Bundle args = new Bundle();
            args.putString("title", "Word puzzle");
            args.putString("message", "You are given phrase in English with shuffled words. Your task is to put words in right order and write down the result.");
            rules.setArguments(args);
            rules.show(getSupportFragmentManager(), "message");
        });

        Button endGameButton = findViewById(R.id.end_game);
        endGameButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("end game", true);
            setResult(RESULT_OK, intent);
            finish();
        });
    }
}
