package ru.hse.android.easyenglish;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

public class WordListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list);

        WordListController wordListController = MainController.getGameController().getWordListController(this);
        List<String> wordListNames = wordListController.getWordLists();

        final ListView wordLists = findViewById(R.id.word_lists);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_multiple_choice, wordListNames);
        wordLists.setAdapter(adapter);

        int position = wordListNames.indexOf(wordListController.getCurrentWordList());
        wordLists.setItemChecked(position, true);

        Button editListsButton = findViewById(R.id.edit_lists_button);
        editListsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WordListActivity.this, WordListEditorActivity.class);
                startActivity(intent);
            }
        });

    }
}
