package ru.hse.android.project.easyenglish;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ru.hse.android.project.easyenglish.adapters.DragAndDropAdapter;
import ru.hse.android.project.easyenglish.adapters.StatisticsAdapter;
import ru.hse.android.project.easyenglish.controllers.MainController;
import ru.hse.android.project.easyenglish.controllers.WordListController;
import ru.hse.android.project.easyenglish.words.Word;

public class StatisticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        WordListController wordListController = MainController.getGameController().getWordListController();
        List<Word> words = wordListController.getCurrentListWords();

        RecyclerView listView = findViewById(R.id.word_list_statistics);

        StatisticsAdapter adapter = new StatisticsAdapter(this, words);
        listView.setAdapter(adapter);

    }
}
