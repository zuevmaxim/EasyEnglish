package ru.hse.android.project.easyenglish;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import ru.hse.android.project.easyenglish.controllers.MainController;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        MainController.init(this);

        Button gamesButton = findViewById(R.id.games_button);
        gamesButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, GamesMenuActivity.class);
            startActivity(intent);
        });

        Button dictionaryButton = findViewById(R.id.dictionary_button);
        dictionaryButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, DictionaryActivity.class);
            startActivity(intent);
        });

        Button statisticsButton = findViewById(R.id.statistics_button);
        statisticsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, StatisticsActivity.class);
            startActivity(intent);
        });

        Button wordListButton = findViewById(R.id.word_list_button);
        wordListButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, WordListActivity.class);
            startActivity(intent);
        });

        Button learnWordsButton = findViewById(R.id.learn_words_button);
        learnWordsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, LearnWordsActivity.class);
            startActivity(intent);
        });
    }
}
