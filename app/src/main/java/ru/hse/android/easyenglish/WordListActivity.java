package ru.hse.android.easyenglish;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

public class WordListActivity extends AppCompatActivity {

    private int currentListNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list);

        final WordListController wordListController = MainController.getGameController().getWordListController(this);
        final List<String> wordListNames = wordListController.getWordLists();

        final ListView wordLists = findViewById(R.id.word_lists);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_multiple_choice, wordListNames);
        wordLists.setAdapter(adapter);

        currentListNumber = wordListNames.indexOf(wordListController.getCurrentWordList());
        wordLists.setItemChecked(currentListNumber, true);
        wordLists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != currentListNumber) {
                    currentListNumber = position;
                    wordListController.setCurrentWordList(wordListNames.get(currentListNumber));
                }
            }
        });

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
