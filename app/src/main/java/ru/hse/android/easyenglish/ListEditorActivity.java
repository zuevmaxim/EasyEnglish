package ru.hse.android.easyenglish;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ListEditorActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_list);

        final ListView wordLists = findViewById(R.id.new_word_list);
        List<Word> newWordList = new ArrayList<>();
        WordAdapter adapter = new WordAdapter(this, R.layout.word_item, newWordList);
        wordLists.setAdapter(adapter);

        Button addNewListButton = findViewById(R.id.add_word_button);
        addNewListButton.setOnClickListener(v -> {
            adapter.addRow();
        });
    }
}
