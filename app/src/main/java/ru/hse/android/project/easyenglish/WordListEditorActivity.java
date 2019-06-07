package ru.hse.android.project.easyenglish;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import java.util.List;

import ru.hse.android.project.easyenglish.adapters.WordListAdapter;
import ru.hse.android.project.easyenglish.controllers.MainController;
import ru.hse.android.project.easyenglish.controllers.WordListController;

/** Shows list of all word lists to update/edit/delete them and choose current word list */
public class WordListEditorActivity extends AppCompatActivity {

    /** Code used in onActivityResult to return from EDIT_LIST_ACTIVITY. */
    private static final int EDIT_LIST_ACTIVITY = 37;

    /** Create activity screen. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list_editor);
        setUpListView();
        Button addNewListButton = findViewById(R.id.add_new_list_button);
        addNewListButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditListActivity.class);
            startActivityForResult(intent, EDIT_LIST_ACTIVITY);
        });
        setResult(RESULT_OK);
    }

    /** Show list word list names. */
    private void setUpListView() {
        WordListController wordListController = MainController.getGameController().getWordListController();
        List<String> wordListNames = wordListController.getWordLists();
        final RecyclerView wordLists = findViewById(R.id.current_word_lists);
        WordListAdapter adapter = new WordListAdapter(this, wordListNames);
        wordLists.setAdapter(adapter);
    }

    /** Accept result from EditListActivity. */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == EDIT_LIST_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                setUpListView();
            }
        }
    }
}