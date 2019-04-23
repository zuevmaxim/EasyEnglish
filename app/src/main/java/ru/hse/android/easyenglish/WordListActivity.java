package ru.hse.android.easyenglish;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class WordListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list);

        WordListController wordListController = MainController.getGameController().getWordListController(this);
        List<String> wordListNames = wordListController.getWordLists();

        WordListAdapter adapter = new WordListAdapter(this, wordListNames);
        final ListView wordLists = findViewById(R.id.word_lists);

        final Context context = this;
        wordLists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(context, "checked", Toast.LENGTH_LONG).show();
                wordLists.setItemChecked(position, true);
            }
        });
        wordLists.setAdapter(adapter);
    }
}
