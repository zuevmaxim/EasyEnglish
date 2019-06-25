package ru.hse.android.project.easyenglish.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.List;

import ru.hse.android.project.easyenglish.R;
import ru.hse.android.project.easyenglish.controllers.MainController;
import ru.hse.android.project.easyenglish.controllers.WordListController;
import ru.hse.android.project.easyenglish.ui.views.adapters.StatisticsAdapter;
import ru.hse.android.project.easyenglish.words.Word;

/** Activity to show statistics on words шin current list */
public class StatisticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        WordListController wordListController = MainController.getGameController().getWordListController();
        List<Word> words = wordListController.getCurrentListWords();

        TextView listNameText = findViewById(R.id.list_name_text);
        listNameText.setText(wordListController.getCurrentWordList().toUpperCase());

        RecyclerView listView = findViewById(R.id.word_list_statistics);
        StatisticsAdapter adapter = new StatisticsAdapter(this, words);
        listView.setAdapter(adapter);

    }
}
