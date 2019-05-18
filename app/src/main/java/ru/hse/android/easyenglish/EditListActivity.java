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
import java.util.stream.Collectors;

import ru.hse.android.easyenglish.adapters.EditWordListAdapter;
import ru.hse.android.easyenglish.controllers.MainController;
import ru.hse.android.easyenglish.controllers.WordListController;
import ru.hse.android.easyenglish.exceptions.WrongListNameException;
import ru.hse.android.easyenglish.exceptions.WrongWordException;
import ru.hse.android.easyenglish.words.Word;

public class EditListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_list);

        Intent intent = getIntent();

        WordListController controller = MainController.getGameController().getWordListController();

        String listName = intent.getStringExtra("list name");
        List<Word> words = controller.getListWords(listName);

        final ListView newWordView = findViewById(R.id.new_word_list);
        List<Pair<Word, EditWordListAdapter.AUTOCHANGES>> wordPairList = new ArrayList<>();

        for (Word word : words) {
            wordPairList.add(new Pair<>(word, EditWordListAdapter.AUTOCHANGES.NONE));
        }


        EditWordListAdapter adapter = new EditWordListAdapter(this, R.layout.editable_word_item, wordPairList);

        newWordView.setAdapter(adapter);

        Button addNewListButton = findViewById(R.id.add_word_button);
        addNewListButton.setOnClickListener(v -> adapter.addRow());

        EditText wordListNameText = findViewById(R.id.new_list_name_text);
        wordListNameText.setText(listName);
        Context context = this;

        Button saveNewListButton = findViewById(R.id.save_list_button);
        saveNewListButton.setOnClickListener(v -> {
            boolean tryAgain = false;
            String wordListName = wordListNameText.getText().toString();
            List<Word> wordList = wordPairList.stream().map(Pair::getKey).collect(Collectors.toList());
            try {
                controller.changeWordList(listName, wordListName, wordList);
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
