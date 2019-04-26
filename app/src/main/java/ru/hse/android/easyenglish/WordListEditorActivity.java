package ru.hse.android.easyenglish;


import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class WordListEditorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list_editor);

        WordListController wordListController = MainController.getGameController().getWordListController();
        List<String> wordListNames = wordListController.getWordLists();

        final ListView wordLists = findViewById(R.id.current_word_lists);
        ArrayAdapter<String> adapter = new WordListAdapter(this,  R.layout.list_item, wordListNames);
        wordLists.setAdapter(adapter);
        final Context context = this;
        wordLists.setOnItemClickListener((parent, view, position, id) -> Toast.makeText(context, "checked", Toast.LENGTH_LONG).show());

        Button addNewListButton = findViewById(R.id.add_new_list_button);
        addNewListButton.setOnClickListener(v -> {
            Intent intent = new Intent(WordListEditorActivity.this, ErrorActivity.class);
            startActivity(intent);
        });

        /*
        expandableListView.setAdapter(getListAdapter(context));
        Button updateRandomListButton = findViewById(R.id.update_random_list_button);
        updateRandomListButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MainController.getGameController().getWordListController(context).updateRandomWordList();
                MainController.getGameController().getWordStorage().updateStorage(context);
                expandableListView.setAdapter(getListAdapter(context));
            }
        });
*/
    }
}