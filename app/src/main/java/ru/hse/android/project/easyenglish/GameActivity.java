package ru.hse.android.project.easyenglish;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
    private final WordListController wordListController = MainController.getGameController().getWordListController();
    private String previousListName;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.e("TAG", "onActivityResult");
        if (requestCode == GAME_RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                if (data == null) {
                    throw new AssertionError();
                }
                boolean endOfGame = data.getBooleanExtra("end game", false);
                if (endOfGame) {
                    endGame();
                } else {
                    final TextView wordAnswerText = findViewById(R.id.game_result_text);
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
                     SynonymsActivity.class
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
        nextWordButton.setOnClickListener(v -> runGame(randomGame(randomGames)));

        final Button finishGameButton = findViewById(R.id.end_game_button);
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
        final TextView gameResult = findViewById(R.id.game_result);
        gameResult.setVisibility(View.GONE);

        final ImageView imageView = findViewById(R.id.result);
        imageView.setVisibility(View.GONE);

        final Button nextWordButton = findViewById(R.id.next_word_button);
        nextWordButton.setVisibility(View.INVISIBLE);

        final Button endGameButton = findViewById(R.id.end_game_button);
        endGameButton.setVisibility(View.INVISIBLE);

        final TextView gameResultText = findViewById(R.id.game_result_text);
        gameResultText.setText(("result : " + succeedTasks + " out of " + totalTasks));

        final Button toMenuButton = findViewById(R.id.to_menu_button);
        toMenuButton.setVisibility(View.VISIBLE);
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
            case "Synonyms" : return SynonymsActivity.class;
        }
        return null;
    }

    public static void onBackPressed(Activity context) {
        new AlertDialog.Builder(context)
                .setTitle("Exiting game")
                .setMessage("Are you sure?")
                .setPositiveButton("YES", (dialog, whichButton) -> {
                    Intent intent = new Intent();
                    intent.putExtra("end game", true);
                    context.setResult(RESULT_OK, intent);
                    context.finish();
                    dialog.dismiss();
                }).setNegativeButton("NO", (dialog, whichButton) -> dialog.dismiss()).show();
    }

    @Override
    public void onStop() {
        if (previousListName != null) {
            wordListController.setCurrentWordList(previousListName);
        }
        super.onStop();
    }
}
