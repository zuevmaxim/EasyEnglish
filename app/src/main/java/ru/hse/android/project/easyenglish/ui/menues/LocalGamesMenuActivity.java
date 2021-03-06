package ru.hse.android.project.easyenglish.ui.menues;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import ru.hse.android.project.easyenglish.R;
import ru.hse.android.project.easyenglish.ui.ChoosePhraseListActivity;
import ru.hse.android.project.easyenglish.ui.GameActivity;

import static ru.hse.android.project.easyenglish.ui.GameActivity.Game.*;

/** Menu for local games. */
public class LocalGamesMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_games_menu);

        Button matchingButton = findViewById(R.id.matching_button);
        matchingButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra(GameActivity.GAME_NAME, MATCHING);
            startActivity(intent);
        });

        Button letterPuzzleButton = findViewById(R.id.letter_puzzle_button);
        letterPuzzleButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra(GameActivity.GAME_NAME, LETTER_PUZZLE);
            startActivity(intent);
        });

        Button wordPuzzleButton = findViewById(R.id.word_puzzle_button);
        wordPuzzleButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChoosePhraseListActivity.class);
            startActivity(intent);
        });

        Button definitionsButton = findViewById(R.id.definitions_button);
        definitionsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra(GameActivity.GAME_NAME, CHOOSE_DEFINITION);
            startActivity(intent);
        });

        Button synonymsButton = findViewById(R.id.synonyms_button);
        synonymsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra(GameActivity.GAME_NAME, SYNONYMS);
            startActivity(intent);
        });
    }
}
