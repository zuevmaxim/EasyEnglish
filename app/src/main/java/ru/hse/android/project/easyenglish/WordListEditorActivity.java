package ru.hse.android.project.easyenglish;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

import ru.hse.android.project.easyenglish.controllers.MainController;
import ru.hse.android.project.easyenglish.adapters.WordListAdapter;
import ru.hse.android.project.easyenglish.controllers.WordListController;

public class WordListEditorActivity extends AppCompatActivity {
    private static final int ADD_NEW_LIST_CODE = 37;
    private static final int EDIT_LIST_CODE = 38;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list_editor);

        setUpListView();

        Button addNewListButton = findViewById(R.id.add_new_list_button);
        addNewListButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddNewListActivity.class);
            startActivityForResult(intent, ADD_NEW_LIST_CODE);
        });
        setResult(RESULT_OK);
    }

    private void setUpListView() {
        WordListController wordListController = MainController.getGameController().getWordListController();
        List<String> wordListNames = wordListController.getWordLists();
        final ListView wordLists = findViewById(R.id.current_word_lists);
        ArrayAdapter<String> adapter = new WordListAdapter(this, R.layout.list_item, wordListNames);
        wordLists.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == ADD_NEW_LIST_CODE || requestCode == EDIT_LIST_CODE) {
            if (resultCode == RESULT_OK) {
                setUpListView();
            }
        }
    }
}