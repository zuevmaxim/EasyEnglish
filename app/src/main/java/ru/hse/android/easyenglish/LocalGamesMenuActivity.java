package ru.hse.android.easyenglish;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LocalGamesMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_games_menu);

        Button matchingButton = findViewById(R.id.matching_button);
        matchingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LocalGamesMenuActivity.this, ErrorActivity.class);
                startActivity(intent);
            }
        });

        Button letterPuzzleButton = findViewById(R.id.letter_puzzle_button);
        letterPuzzleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LocalGamesMenuActivity.this, LetterPuzzleActivity.class);
                startActivity(intent);
            }
        });

        Button wordPuzzleButton = findViewById(R.id.word_puzzle_button);
        wordPuzzleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LocalGamesMenuActivity.this, ErrorActivity.class);
                startActivity(intent);
            }
        });

        Button definitionsButton = findViewById(R.id.definitions_button);
        definitionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LocalGamesMenuActivity.this, ErrorActivity.class);
                startActivity(intent);
            }
        });

        Button synonymsButton = findViewById(R.id.synonyms_button);
        synonymsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LocalGamesMenuActivity.this, ErrorActivity.class);
                startActivity(intent);
            }
        });
    }
}
