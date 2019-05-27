package ru.hse.android.project.easyenglish;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;

import java.util.Random;

import ru.hse.android.project.easyenglish.controllers.MainController;
import ru.hse.android.project.easyenglish.controllers.WordListController;

public class GameActivity extends AppCompatActivity {
    private int succeedTasks = 0;
    private int totalTasks = 0;
    private static final int GAME_RESULT_CODE = 42;
    private static final Random RANDOM = new Random();
    private WordListController wordListController = MainController.getGameController().getWordListController();
    private String previousListName;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.e("TAG", "onActivityResult");
        if (requestCode == GAME_RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                boolean endOfGame = data.getBooleanExtra("end game", false);
                if (endOfGame) {
                    endGame();
                } else {
                    final TextView wordAnswerText = findViewById(R.id.word_answer);
                    wordAnswerText.setVisibility(View.VISIBLE);
                    String word = data.getStringExtra("word");
                    wordAnswerText.setText(word);

                    boolean result = data.getBooleanExtra("game result", false);
                    Drawable drawable;
                    ImageView imageView = findViewById(R.id.result);
                    final TextView gameResultText = findViewById(R.id.game_result);
                    if (result) {
                        gameResultText.setTextColor(Color.parseColor("#FF00574B"));
                        gameResultText.setText(getString(R.string.right_answer));
                        drawable = getResources().getDrawable(R.drawable.right, null);
                        imageView.setImageDrawable(drawable);
                    } else {
                        gameResultText.setTextColor(Color.parseColor("#FFD81B60"));
                        gameResultText.setText(getString(R.string.wrong_answer));
                        drawable = getResources().getDrawable(R.drawable.wrong, null);
                        imageView.setImageDrawable(drawable);
                    }
                    succeedTasks += result ? 1 : 0;
                    totalTasks++;
                }
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

         Class<?>[] randomGames;
         if (gameName.equals("10 Words")) {
             previousListName = wordListController.getCurrentWordList();
             wordListController.setCurrentRandomWordList();

            randomGames = new Class<?>[]{
                     LetterPuzzleActivity.class,
                     ChooseDefinitionActivity.class,
                     MatchingActivity.class,
                     WordPuzzleActivity.class
                     // SynonymsActivity.class  TODO
             };
         } else {
             randomGames = new Class<?>[]{gameClass};
         }

        succeedTasks = 0;
        totalTasks = 0;

        runGame(randomGame(randomGames));

        final Button toMenuButton = findViewById(R.id.to_menu_button);
        toMenuButton.setVisibility(View.INVISIBLE);
        final Button nextWordButton = findViewById(R.id.next_word_button);
        final Button finishGameButton = findViewById(R.id.finish_game_button);

        nextWordButton.setOnClickListener(v -> runGame(randomGame(randomGames)));

        finishGameButton.setOnClickListener(v -> endGame());
    }

    private Class<?> randomGame(Class<?>[] randomGames) {
        return randomGames[RANDOM.nextInt(randomGames.length)];
    }

    private void runGame(Class<?> gameClass) {
        Intent intent = new Intent(this, gameClass);
        startActivityForResult(intent, GAME_RESULT_CODE);
    }

    private void endGame() {
        final Button nextWordButton = findViewById(R.id.next_word_button);
        final Button finishGameButton = findViewById(R.id.finish_game_button);
        final TextView gameResult = findViewById(R.id.game_result);
        final TextView wordAnswerText = findViewById(R.id.word_answer);
        wordAnswerText.setVisibility(View.INVISIBLE);
        gameResult.setVisibility(View.GONE);
        ImageView imageView = findViewById(R.id.result);
        imageView.setVisibility(View.GONE);
        nextWordButton.setVisibility(View.INVISIBLE);
        finishGameButton.setVisibility(View.INVISIBLE);
        final Button toMenuButton = findViewById(R.id.to_menu_button);
        final TextView gameResultText = findViewById(R.id.game_result_text);
        toMenuButton.setVisibility(View.VISIBLE);
        gameResultText.setText(("result : " + succeedTasks + " out of " + totalTasks));

        toMenuButton.setOnClickListener(v -> {
            Intent intent = new Intent(GameActivity.this, MainMenuActivity.class);
            startActivity(intent);
        });
    }

    private Class<?> chooseGameByName(String gameName) {
        switch (gameName) {
            case "Letter Puzzle" : return LetterPuzzleActivity.class;
            case "Choose Definition" : return ChooseDefinitionActivity.class;
            case "Word Puzzle" : return WordPuzzleActivity.class;
            case "Matching" : return MatchingActivity.class;
        }
        return null;
    }

    @Override
    public void onStop() {
        if (previousListName != null) {
            wordListController.setCurrentWordList(previousListName);
        }
        super.onStop();
    }
}
