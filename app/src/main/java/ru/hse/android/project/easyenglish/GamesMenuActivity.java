package ru.hse.android.project.easyenglish;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import ru.hse.android.project.easyenglish.games.WordChainActivity;

/** Menu for games. */
public class GamesMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games_menu);

        Button words10Button = findViewById(R.id._10words_button);
        words10Button.setOnClickListener(v -> {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra(GameActivity.GAME_NAME, "10 Words");
            startActivity(intent);
        });

        Button localGamesButton = findViewById(R.id.local_games_button);
        localGamesButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, LocalGamesMenuActivity.class);
            startActivity(intent);
        });

        Button wordChainButton = findViewById(R.id.word_chain_button);
        wordChainButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, WordChainActivity.class);
            startActivity(intent);
        });
    }
}
