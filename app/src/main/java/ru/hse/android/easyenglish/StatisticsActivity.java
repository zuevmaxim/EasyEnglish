package ru.hse.android.easyenglish;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        WordListController wordListController = MainController.getGameController().getWordListController();
        WordFactory wordFactory = MainController.getGameController().getWordFactory();
        List<Word> words = wordListController.getCurrentListWords();
        List<String> statistics = new ArrayList<>();
        for (Word word : words) {
            int errorNumber = wordFactory.getWordErrorNumber(word);
            int totalNumber = wordFactory.getWordTotalNumber(word);
            statistics.add(word.getRussian() + " - " + word.getEnglish() + " " + (totalNumber - errorNumber) + "/" + totalNumber);
        }

        ListView listView = findViewById(R.id.word_list_statistics);

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, statistics);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
