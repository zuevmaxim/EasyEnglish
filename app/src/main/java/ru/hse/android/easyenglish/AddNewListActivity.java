package ru.hse.android.easyenglish;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class AddNewListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_list);

        ListView newWordsListView = findViewById(R.id.new_word_lists);
        final EditText russianWordText = findViewById(R.id.new_russian_word_text);
        final EditText englishWordText = findViewById(R.id.new_english_word_text);

        final ArrayList<String> newList = new ArrayList<>();
        final ArrayList<Word> newWordsList = new ArrayList<>();
        final ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, newList);

        newWordsListView.setAdapter(adapter);

        Button addWord = findViewById(R.id.add_word_button);
        addWord.setOnClickListener(v -> {
            String russianWord = russianWordText.getText().toString();
            String englishWord = englishWordText.getText().toString();
            if (russianWord.isEmpty()) {
                russianWord = TranslateController.translate(englishWord, "en-ru");
            }
            if (englishWord.isEmpty()) {
                englishWord = TranslateController.translate(russianWord, "ru-en");
            }
            newList.add(0, russianWord + " - " + englishWord);
            newWordsList.add(new Word(russianWord, englishWord));
            adapter.notifyDataSetChanged();
            russianWordText.setText("");
            englishWordText.setText("");
        });

        final EditText newWordLIstNameText = findViewById(R.id.new_list_name_text);
        Button saveWordList = findViewById(R.id.save_list_button);
        saveWordList.setOnClickListener(v -> {
            String newWordLIstName = newWordLIstNameText.getText().toString();
            WordListController controller = MainController.getGameController().getWordListController();
            controller.addNewWordList(newWordLIstName, newWordsList);
            Intent intent = new Intent( AddNewListActivity.this, WordListActivity.class);
            startActivity(intent);
        });
    }
}
