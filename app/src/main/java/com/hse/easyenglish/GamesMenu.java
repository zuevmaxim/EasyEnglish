package com.hse.easyenglish;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class GamesMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_games);

        Button words10 = findViewById(R.id.button_10words);
        words10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GamesMenu.this, ErrorActivity.class);
                startActivity(intent);
            }
        });

        Button localGames = findViewById(R.id.button_local_games);
        localGames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GamesMenu.this, LocalGames.class);
                startActivity(intent);
            }
        });

        Button wordChain = findViewById(R.id.button_word_chain);
        wordChain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GamesMenu.this, ErrorActivity.class);
                startActivity(intent);
            }
        });
    }
}
