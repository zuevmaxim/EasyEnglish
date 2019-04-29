package ru.hse.android.easyenglish;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.List;

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
