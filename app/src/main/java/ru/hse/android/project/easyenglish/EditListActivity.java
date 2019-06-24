package ru.hse.android.project.easyenglish;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ru.hse.android.project.easyenglish.adapters.EditWordListAdapter;
import ru.hse.android.project.easyenglish.controllers.MainController;
import ru.hse.android.project.easyenglish.controllers.TranslateController;
import ru.hse.android.project.easyenglish.controllers.WordListController;
import ru.hse.android.project.easyenglish.exceptions.WrongListNameException;
import ru.hse.android.project.easyenglish.exceptions.WrongWordException;
import ru.hse.android.project.easyenglish.words.Word;

/** Activity to create and edit word lists. */
public class EditListActivity extends AppCompatActivity {

    private final WordListController controller = MainController.getGameController().getWordListController();

    /** Create listView with editable text fields to set words in. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list);
        Intent intent = getIntent();

        final RecyclerView wordListView = findViewById(R.id.word_list);
        EditText wordListNameText = findViewById(R.id.list_name_text);
        List<Pair<Word, EditWordListAdapter.AUTO_CHANGES>> wordPairList = new ArrayList<>();

        String listName = intent.getStringExtra("list name");
        boolean isNewList = listName == null;
        if (isNewList) {
            wordPairList.add(new Pair<>(new Word("", ""), EditWordListAdapter.AUTO_CHANGES.BOTH));
        } else {
            wordListNameText.setText(listName);
            List<Word> words = controller.getListWords(listName);
            for (Word word : words) {
                wordPairList.add(new Pair<>(word, EditWordListAdapter.AUTO_CHANGES.NONE));
            }
        }

        EditWordListAdapter adapter = new EditWordListAdapter(this, wordPairList);
        wordListView.setAdapter(adapter);
        Context context = this;

        Button addNewWordButton = findViewById(R.id.add_word_button);
        addNewWordButton.setOnClickListener(v -> {
            wordPairList.add(new Pair<>(new Word("", ""), EditWordListAdapter.AUTO_CHANGES.BOTH));
            adapter.notifyItemInserted(wordPairList.size() - 1);
            wordListView.scrollToPosition(wordPairList.size() - 1);
        });

        Button saveNewListButton = findViewById(R.id.save_list_button);
        saveNewListButton.setOnClickListener(v -> {
            boolean tryAgain = false;
            String wordListName = wordListNameText.getText().toString();
            List<Word> wordList = wordPairList
                    .stream()
                    .map(pair -> pair.first)
                    .peek(word -> {
                        if (word.getTranscription().isEmpty()) {
                            word.setTranscription(TranslateController.wordInfo(word.getEnglish()).getTranscription());
                        }
                    })
                    .collect(Collectors.toList());
            try {
                if (isNewList) {
                    controller.addNewWordList(wordListName, wordList);
                } else {
                    controller.changeWordList(listName, wordListName, wordList);
                }
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
