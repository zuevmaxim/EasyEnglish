package ru.hse.android.easyenglish;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AddNewListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_list);

        final ListView newWordView = findViewById(R.id.new_word_list);
        List<Pair<Word, WordAdapter.AUTOCHANGES>> newWordList = new ArrayList<>();
        WordAdapter adapter = new WordAdapter(this, R.layout.word_item, newWordList);
        adapter.addRow();
        newWordView.setAdapter(adapter);

        Button addNewListButton = findViewById(R.id.add_word_button);
        addNewListButton.setOnClickListener(v -> adapter.addRow());

        WordListController controller = MainController.getGameController().getWordListController();
        EditText newWordListNameText = findViewById(R.id.new_list_name_text);
        Context context = this;

        Button saveNewListButton = findViewById(R.id.save_list_button);
        saveNewListButton.setOnClickListener(v -> {
            boolean tryAgain = false;
            String newWordListName = newWordListNameText.getText().toString();
            List<Word> wordList = newWordList.stream().map(Pair::getKey).collect(Collectors.toList());
            try {
                controller.addNewWordList(newWordListName, wordList);
            } catch (WrongWordException | WrongListNameException e) {
                tryAgain = true;
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
            if (!tryAgain) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }
}
