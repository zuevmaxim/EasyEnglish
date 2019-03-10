package com.hse.easyenglish;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class LocalGames extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.local_games);

        Button matching = findViewById(R.id.button_matching);
        matching.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LocalGames.this, ErrorActivity.class);
                startActivity(intent);
            }
        });

        Button letterPuzzle = findViewById(R.id.button_letter_puzzle);
        letterPuzzle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LocalGames.this, ErrorActivity.class);
                startActivity(intent);
            }
        });

        Button wordPuzzle = findViewById(R.id.button_word_puzzle);
        wordPuzzle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LocalGames.this, ErrorActivity.class);
                startActivity(intent);
            }
        });

        Button definitions = findViewById(R.id.button_definitions);
        definitions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LocalGames.this, ErrorActivity.class);
                startActivity(intent);
            }
        });

        Button synonyms = findViewById(R.id.button_synonyms);
        synonyms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LocalGames.this, ErrorActivity.class);
                startActivity(intent);
            }
        });
    }
}
