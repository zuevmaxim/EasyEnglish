package ru.hse.android.easyenglish;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

import ru.hse.android.easyenglish.controllers.MainController;
import ru.hse.android.easyenglish.controllers.WordListController;

public class WordListActivity extends AppCompatActivity {
    private static final int SET_LISTS_CODE = 23;

    private int currentListNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list);

        setUpListView();

        Button editListsButton = findViewById(R.id.edit_lists_button);
        editListsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, WordListEditorActivity.class);
            startActivityForResult(intent, SET_LISTS_CODE);
        });
    }

    private void setUpListView() {
        final WordListController wordListController = MainController.getGameController().getWordListController();
        final List<String> wordListNames = wordListController.getWordLists();

        final ListView wordLists = findViewById(R.id.word_lists);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_single_choice, wordListNames);
        wordLists.setAdapter(adapter);

        currentListNumber = wordListNames.indexOf(wordListController.getCurrentWordList());
        wordLists.setItemChecked(currentListNumber, true);
        wordLists.setOnItemClickListener((parent, view, position, id) -> {
            if (position != currentListNumber) {
                currentListNumber = position;
                wordListController.setCurrentWordList(wordListNames.get(currentListNumber));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == SET_LISTS_CODE) {
            if (resultCode == RESULT_OK) {
                setUpListView();
            }
        }
    }
}
