package ru.hse.android.easyenglish;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GamesMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games_menu);

        Button words10Button = findViewById(R.id._10words_button);
        words10Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GamesMenuActivity.this, ErrorActivity.class);
                startActivity(intent);
            }
        });

        Button localGamesButton = findViewById(R.id.local_games_button);
        localGamesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GamesMenuActivity.this, LocalGamesMenuActivity.class);
                startActivity(intent);
            }
        });

        Button wordChainButton = findViewById(R.id.word_chain_button);
        wordChainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GamesMenuActivity.this, ErrorActivity.class);
                startActivity(intent);
            }
        });
    }
}
