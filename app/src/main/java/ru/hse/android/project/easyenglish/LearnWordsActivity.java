package ru.hse.android.project.easyenglish;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;
import java.util.stream.Collectors;

import ru.hse.android.project.easyenglish.controllers.MainController;
import ru.hse.android.project.easyenglish.controllers.WordListController;

public class LearnWordsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_words);

        WordListController wordListController = MainController.getGameController().getWordListController();
        List<String> words = wordListController.getCurrentListWords().stream()
                .map(word -> word.getRussian() + " - " + word.getEnglish() + " " + word.getTranscription())
                .collect(Collectors.toList());

        final ListView currentWordList = findViewById(R.id.learn_words_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, words);
        currentWordList.setAdapter(adapter);
    }
}