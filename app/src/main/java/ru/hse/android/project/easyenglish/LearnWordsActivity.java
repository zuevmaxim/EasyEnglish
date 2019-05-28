package ru.hse.android.project.easyenglish;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import java.util.List;

import ru.hse.android.project.easyenglish.adapters.LearnWordsListAdapter;
import ru.hse.android.project.easyenglish.controllers.MainController;
import ru.hse.android.project.easyenglish.controllers.WordListController;
import ru.hse.android.project.easyenglish.words.Word;

public class LearnWordsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_words);

        WordListController wordListController = MainController.getGameController().getWordListController();
        List<Word> words = wordListController.getCurrentListWords();

        RecyclerView currentWordList = findViewById(R.id.learn_words_list);
        LearnWordsListAdapter adapter = new LearnWordsListAdapter(this, words);
        currentWordList.setAdapter(adapter);
    }
}