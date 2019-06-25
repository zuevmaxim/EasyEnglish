package ru.hse.android.project.easyenglish.ui.menues;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import ru.hse.android.project.easyenglish.R;
import ru.hse.android.project.easyenglish.controllers.MainController;
import ru.hse.android.project.easyenglish.ui.DictionaryActivity;
import ru.hse.android.project.easyenglish.ui.LearnWordsActivity;
import ru.hse.android.project.easyenglish.ui.StatisticsActivity;
import ru.hse.android.project.easyenglish.ui.WordListEditorActivity;

/** Main menu. */
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
            Intent intent = new Intent(this, WordListEditorActivity.class);
            startActivity(intent);
        });

        Button learnWordsButton = findViewById(R.id.learn_words_button);
        learnWordsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, LearnWordsActivity.class);
            startActivity(intent);
        });
    }
}
