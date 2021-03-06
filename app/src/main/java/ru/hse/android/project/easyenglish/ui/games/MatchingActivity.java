package ru.hse.android.project.easyenglish.ui.games;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

import ru.hse.android.project.easyenglish.R;
import ru.hse.android.project.easyenglish.logic.MatchingLogic;
import ru.hse.android.project.easyenglish.ui.GameActivity;
import ru.hse.android.project.easyenglish.ui.views.DragAndDropListView;
import ru.hse.android.project.easyenglish.ui.views.adapters.DragAndDropAdapter;
import ru.hse.android.project.easyenglish.words.Word;

/**
 * Activity for MatchingActivity game with logic described in MatchingLogic.
 */
public class MatchingActivity extends AppCompatActivity {

    private final MatchingLogic logic = new MatchingLogic();

    /** Create game screen with list of Russian words and list with shuffled English translations. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching);

        logic.update();

        final List<String> russianWords = logic.getRussianWords();
        final List<String> shuffledEnglishWords = logic.getShuffledEnglishWords();

        ListView listView = findViewById(R.id.matching_list);
        DragAndDropAdapter adapter = new DragAndDropAdapter(this, russianWords, R.layout.matching_item);
        listView.setAdapter(adapter);

        DragAndDropListView dragListView = findViewById(R.id.matching_drag_and_drop_list);
        DragAndDropAdapter dragListAdapter = new DragAndDropAdapter(this, shuffledEnglishWords,  R.layout.matching_item);
        dragListView.setAdapter(dragListAdapter);

        Button checkAnswerButton = findViewById(R.id.send_answer_button);
        checkAnswerButton.setOnClickListener(v -> checkAnswer(shuffledEnglishWords));

        GameActivity.initRulesButton(this,
                getString(R.string.rules_matching),
                getString(R.string.rules_matching_text));

        GameActivity.initHintsButton(this,
                getString(R.string.matching_hints),
                logic.getHint().getRussian() + " - " + logic.getHint().getEnglish());

        GameActivity.initEndGameButton(this);
    }

    /** Check if given answer equals to model and send report to GameActivity. */
    private void checkAnswer(@NonNull List<String> givenAnswer) {
        boolean result = logic.checkAnswer(givenAnswer);
        Intent intent = new Intent();
        intent.putExtra(GameActivity.GAME_RESULT_TAG, result);
        StringBuilder answerText = new StringBuilder();
        List<Word> answer = logic.getAnswer();
        for (Word word : answer) {
            answerText.append(word.getRussian()).append(" - ").append(word.getEnglish()).append("\n");
        }
        intent.putExtra(GameActivity.MESSAGE_TAG, answerText.toString());
        setResult(RESULT_OK, intent);
        finish();
    }

    /** On back button pressed ask player if he want to end the game. */
    @Override
    public void onBackPressed() {
        GameActivity.onBackPressed(this);
    }
}
