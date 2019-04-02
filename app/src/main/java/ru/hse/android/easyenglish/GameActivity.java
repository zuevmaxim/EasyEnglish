package ru.hse.android.easyenglish;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;

public class GameActivity extends AppCompatActivity {
    private int succeedTasks = 0;
    private int totalTasks = 0;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 42) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                boolean result = data.getBooleanExtra("game result", false);
                Drawable drawable;
                ImageView imageView = findViewById(R.id.result);
                final TextView gameResultText = findViewById(R.id.game_result);
                if (result) {
                    gameResultText.setTextColor(Color.parseColor("#FF00574B"));
                    gameResultText.setText("RIGHT!");
                    drawable = getResources().getDrawable(R.drawable.right,null);
                    imageView.setImageDrawable(drawable);
                } else {
                    gameResultText.setTextColor(Color.parseColor("#FFD81B60"));
                    gameResultText.setText("WRONG!");
                    drawable = getResources().getDrawable(R.drawable.wrong,null);
                    imageView.setImageDrawable(drawable);
                }
                succeedTasks += result ? 1 : 0;
                totalTasks++;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();

        String gameName = intent.getStringExtra("game name");
        final Class<?> gameClass = chooseGameByName(gameName);
        succeedTasks = 0;
        totalTasks = 0;

        runGame(gameClass);

        final Button toGamesButton = findViewById(R.id.to_games_button);
        toGamesButton.setVisibility(View.INVISIBLE);
        final Button nextWordButton = findViewById(R.id.next_word_button);
        final Button finishGameButton = findViewById(R.id.finish_game_button);
        final TextView gameResultText = findViewById(R.id.game_result_text);

        nextWordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runGame(gameClass);
            }
        });

        finishGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView imageView = findViewById(R.id.result);
                imageView.setVisibility(View.GONE);
                nextWordButton.setVisibility(View.INVISIBLE);
                finishGameButton.setVisibility(View.INVISIBLE);
                toGamesButton.setVisibility(View.VISIBLE);
                gameResultText.setText(("result : " + succeedTasks + " out of " + totalTasks));
            }
        });

        toGamesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameActivity.this, LocalGamesMenuActivity.class);
                startActivity(intent);
            }
        });
    }

    private void runGame(Class<?> gameClass) {
        Intent intent = new Intent(GameActivity.this, gameClass);
        startActivityForResult(intent, 42);
    }

    private Class<?> chooseGameByName(String gameName) {
        switch (gameName) {
            case "Letter Puzzle" : return LetterPuzzleActivity.class;
        }
        return null;
    }
}
