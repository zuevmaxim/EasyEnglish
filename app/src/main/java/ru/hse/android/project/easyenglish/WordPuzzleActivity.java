package ru.hse.android.project.easyenglish;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ru.hse.android.project.easyenglish.adapters.DragAndDropAdapter;
import ru.hse.android.project.easyenglish.controllers.MainController;
import ru.hse.android.project.easyenglish.controllers.Phrase;
import ru.hse.android.project.easyenglish.controllers.PhraseStorage;

public class WordPuzzleActivity extends AppCompatActivity {

    private boolean result;

    private List<String> shuffleWords(Phrase phrase) {
        String englishPhrase = phrase.getEnglish();
        List<String> words = Arrays.asList(englishPhrase.split(" "));
        List<String> shuffledWordResult = new ArrayList<>(words);
        while (words.equals(shuffledWordResult)) {
            Collections.shuffle(shuffledWordResult);
        }
        return shuffledWordResult;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_puzzle);

        PhraseStorage storage = MainController.getGameController().getPhraseStorage();
        Phrase phrase = storage.nextPhrase();
        List<String> words = shuffleWords(phrase);
        for (String word : words) {
            System.out.println(word);
        }

        DragAndDropListView dragListView = findViewById(R.id.drag_and_drop_list);
        DragAndDropAdapter dragListAdapter = new DragAndDropAdapter(this, words);
        dragListView.setAdapter(dragListAdapter);

        Button checkAnswerButton = findViewById(R.id.check_answer);

        checkAnswerButton.setOnClickListener(v -> {
            for (String word : words) {
                System.out.println(word);
                /*
                Intent intent = new Intent();
                intent.putExtra("end game", true);
                setResult(RESULT_OK, intent);
                finish();
                */
            }
        });
    }
}
