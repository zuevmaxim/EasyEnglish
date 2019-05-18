package ru.hse.android.easyenglish;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.List;

import ru.hse.android.easyenglish.adapters.StatisticsAdapter;
import ru.hse.android.easyenglish.controllers.MainController;
import ru.hse.android.easyenglish.controllers.WordListController;
import ru.hse.android.easyenglish.words.Word;

public class StatisticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        WordListController wordListController = MainController.getGameController().getWordListController();
        List<Word> words = wordListController.getCurrentListWords();

        ListView listView = findViewById(R.id.word_list_statistics);

        StatisticsAdapter adapter = new StatisticsAdapter(this, R.layout.statistics_item, words);
        listView .setAdapter(adapter);
    }
}
