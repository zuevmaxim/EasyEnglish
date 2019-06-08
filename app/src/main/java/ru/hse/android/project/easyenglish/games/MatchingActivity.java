package ru.hse.android.project.easyenglish.games;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

import ru.hse.android.project.easyenglish.DragAndDropListView;
import ru.hse.android.project.easyenglish.GameActivity;
import ru.hse.android.project.easyenglish.R;
import ru.hse.android.project.easyenglish.ShowInfoActivity;
import ru.hse.android.project.easyenglish.adapters.DragAndDropAdapter;
import ru.hse.android.project.easyenglish.games.logic.MatchingLogic;
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

        Button showHintsButton = findViewById(R.id.hints_button);
        showHintsButton.setOnClickListener(v -> {
            ShowInfoActivity rules = new ShowInfoActivity();
            Bundle args = new Bundle();
            args.putString("title", "Matching");
            Word hint = logic.getHint();
            args.putString("message", hint.getEnglish() + " - " + hint.getRussian());
            rules.setArguments(args);
            rules.show(getSupportFragmentManager(), "message");
        });

        Button showRulesButton = findViewById(R.id.rules_button);
        showRulesButton.setOnClickListener(v -> {
            ShowInfoActivity rules = new ShowInfoActivity();
            Bundle args = new Bundle();
            args.putString("title", getString(R.string.rules_matching));
            args.putString("message", getString(R.string.rules_matching_text));
            rules.setArguments(args);
            rules.show(getSupportFragmentManager(), "message");
        });

        Button endGameButton = findViewById(R.id.end_game_button);
        endGameButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("end game", true);
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    /** Check if given answer equals to model and send report to GameActivity. */
    private void checkAnswer(List<String> givenAnswer) {
        boolean result = logic.checkAnswer(givenAnswer);
        Intent intent = new Intent();
        intent.putExtra("game result", result);
        StringBuilder answerText = new StringBuilder();
        List<Word> answer = logic.getAnswer();
        for (Word word : answer) {
            answerText.append(word.getRussian()).append(" - ").append(word.getEnglish()).append("\n");
        }
        intent.putExtra("word", answerText.toString());
        setResult(RESULT_OK, intent);
        finish();
    }

    /** On back button pressed ask player if he want to end the game. */
    @Override
    public void onBackPressed() {
        GameActivity.onBackPressed(this);
    }
}
