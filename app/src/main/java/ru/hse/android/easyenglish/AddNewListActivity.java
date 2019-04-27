package ru.hse.android.easyenglish;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

public class AddNewListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_list);

        ListView newWordsListView = findViewById(R.id.new_word_lists);
        final EditText russianWordText = findViewById(R.id.new_russian_word_text);
        final EditText englishWordText = findViewById(R.id.new_english_word_text);

        final List<String> newList = new LinkedList<>();
        final List<Word> newWordsList = new LinkedList<>();
        final ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, newList);

        newWordsListView.setAdapter(adapter);
        Context context = this;
        Button addWord = findViewById(R.id.add_word_button);
        addWord.setOnClickListener(v -> {
            String russianWord = russianWordText.getText().toString();
            String englishWord = englishWordText.getText().toString();
            Boolean tryAgain = false;
            if (russianWord.isEmpty() && englishWord.isEmpty()) {
                Toast.makeText(context, "Enter word", Toast.LENGTH_LONG).show();
            } else {
                if (russianWord.isEmpty()) {
                    russianWord = TranslateController.translate(englishWord, "en-ru");
                }
                if (englishWord.isEmpty()) {
                    englishWord = TranslateController.translate(russianWord, "ru-en");
                }
                if (!russianWord.matches("[А-Яа-я\\s]+") || !englishWord.matches("[A-Za-z\\s]+")) {
                    Toast.makeText(context, "Words should only contains letters and spaces. Check your spelling.", Toast.LENGTH_LONG).show();
                } else {
                    newWordsList.add(new Word(russianWord, englishWord));
                    newList.add(0, russianWord + " - " + englishWord);
                    adapter.notifyDataSetChanged();
                    russianWordText.setText("");
                    englishWordText.setText("");
                }
            }
        });


        WordListController controller = MainController.getGameController().getWordListController();
        final EditText newWordLIstNameText = findViewById(R.id.new_list_name_text);
        Button saveWordList = findViewById(R.id.save_list_button);
        saveWordList.setOnClickListener(v -> {
            boolean tryAgain;
            String newWordLIstName = newWordLIstNameText.getText().toString();
            try {
                tryAgain = false;
                controller.addNewWordList(newWordLIstName, newWordsList);
            } catch (Exception e) {
                tryAgain = true;
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
            if (!tryAgain) {
                Intent intent = new Intent(this, WordListActivity.class);
                startActivity(intent);
            }
        });
    }
}
