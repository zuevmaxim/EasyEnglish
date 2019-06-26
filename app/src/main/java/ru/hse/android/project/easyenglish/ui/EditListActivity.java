package ru.hse.android.project.easyenglish.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ru.hse.android.project.easyenglish.R;
import ru.hse.android.project.easyenglish.controllers.MainController;
import ru.hse.android.project.easyenglish.controllers.TranslateController;
import ru.hse.android.project.easyenglish.controllers.WordListController;
import ru.hse.android.project.easyenglish.exceptions.WrongListNameException;
import ru.hse.android.project.easyenglish.exceptions.WrongWordException;
import ru.hse.android.project.easyenglish.ui.views.adapters.EditWordListAdapter;
import ru.hse.android.project.easyenglish.words.Word;

/** Activity to create and edit word lists. */
public class EditListActivity extends AppCompatActivity {

    /** Tag to put extra list name to intent. */
    public static final String LIST_NAME_TAG = "list name";

    /** Previous name of list or null? if list is new one. */
    private String oldListName;

    private final WordListController controller = MainController.getGameController().getWordListController();

    /** Create listView with editable text fields to set words in. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list);
        Intent intent = getIntent();

        List<Pair<Word, EditWordListAdapter.AUTO_CHANGES>> wordPairList = new ArrayList<>();

        oldListName = intent.getStringExtra(LIST_NAME_TAG);
        if (oldListName == null) {
            wordPairList.add(new Pair<>(new Word("", ""), EditWordListAdapter.AUTO_CHANGES.BOTH));
        } else {
            ((TextView) findViewById(R.id.list_name_text)).setText(oldListName);
            List<Word> words = controller.getListWords(oldListName);
            for (Word word : words) {
                wordPairList.add(new Pair<>(word, EditWordListAdapter.AUTO_CHANGES.NONE));
            }
        }

        final RecyclerView wordListView = findViewById(R.id.word_list);
        EditWordListAdapter adapter = new EditWordListAdapter(this, wordPairList);
        wordListView.setAdapter(adapter);

        Button addNewWordButton = findViewById(R.id.add_word_button);
        addNewWordButton.setOnClickListener(v -> {
            wordPairList.add(new Pair<>(new Word("", ""), EditWordListAdapter.AUTO_CHANGES.BOTH));
            adapter.notifyItemInserted(wordPairList.size() - 1);
            wordListView.scrollToPosition(wordPairList.size() - 1);
        });

        Button saveNewListButton = findViewById(R.id.save_list_button);
        saveNewListButton.setOnClickListener(v -> saveWordList(wordPairList));
    }

    /** Save edited word list to database. */
    private void saveWordList(@NonNull List<Pair<Word, EditWordListAdapter.AUTO_CHANGES>> wordPairList) {
        boolean tryAgain = false;
        String newListName = ((TextView) findViewById(R.id.list_name_text)).getText().toString();
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
            if (oldListName == null) {
                controller.addNewWordList(newListName, wordList);
            } else {
                controller.changeWordList(oldListName, newListName, wordList);
            }
        } catch (WrongWordException | WrongListNameException e) {
            tryAgain = true;
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        if (!tryAgain) {
            setResult(RESULT_OK);
            finish();
        }
    }
}
