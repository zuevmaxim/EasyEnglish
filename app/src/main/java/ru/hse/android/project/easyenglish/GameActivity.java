package ru.hse.android.project.easyenglish;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ru.hse.android.project.easyenglish.controllers.MainController;
import ru.hse.android.project.easyenglish.controllers.WordListController;
import ru.hse.android.project.easyenglish.games.ChooseDefinitionActivity;
import ru.hse.android.project.easyenglish.games.LetterPuzzleActivity;
import ru.hse.android.project.easyenglish.games.MatchingActivity;
import ru.hse.android.project.easyenglish.games.SynonymsActivity;
import ru.hse.android.project.easyenglish.games.WordPuzzleActivity;

/**
 * GameActivity is common for all local games.
 * It starts a game and shows the result.
 * In case of 10words game, local games comes in random order.
 */
public class GameActivity extends AppCompatActivity {

    /** Right answers counter. */
    private int succeedTasks = 0;

    /** Wrong answers counter. */
    private int totalTasks = 0;

    /** Code for starting a game is used in onActivityResult method. */
    private static final int GAME_RESULT_CODE = 42;

    private static final Random RANDOM = new Random();
    private final WordListController wordListController = MainController.getGameController().getWordListController();

    /**
     * In case of 10 words game, day list is set as current automatically.
     * When finishing the game, prevous list is recovered as current word list.
     */
    private String previousListName;

    /**
     * If there is no internet connection Synonyms activity returns such result,
     * on which this game is removed from games list.
     */
    public final static int RESULT_REMOVE_SYNONYMS = 999;

    /** Tag to put extra game result to intent. */
    public static final String GAME_RESULT_TAG = "game result";

    /** Tag to put extra word to intent. */
    public static final String WORD_TAG = "word";

    /** Tag to put extra end game flag to intent. */
    public static final String END_GAME_TAG = "word";

    /** Tad to put extra game name. */
    public static final String GAME_NAME = "game name";

    /**
     * Games list to play.
     * Contains several games in case of 10 words game, and only one game otherwise.
     */
    private List<Class<?>> randomGames;

    /** Getting result from a game. */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.e("TAG", "onActivityResult");
        if (requestCode == GAME_RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                if (data == null) {
                    throw new AssertionError();
                }
                boolean endOfGame = data.getBooleanExtra(END_GAME_TAG, false);
                if (endOfGame) {
                    endGame();
                } else {
                    final TextView wordAnswerText = findViewById(R.id.game_result_text);
                    wordAnswerText.setVisibility(View.VISIBLE);

                    String word = data.getStringExtra(WORD_TAG);
                    wordAnswerText.setText(word);

                    boolean result = data.getBooleanExtra(GAME_RESULT_TAG, false);
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
            } else if (resultCode == RESULT_REMOVE_SYNONYMS) {
                randomGames.remove(SynonymsActivity.class);
                if (randomGames.isEmpty()) {
                    new AlertDialog.Builder(this)
                            .setMessage(this.getString(R.string.check_internet_connect))
                            .setCancelable(false)
                            .setNeutralButton(android.R.string.ok, (dialogInterface, i) -> finish())
                            .show();
                    return;
                }
                runGame(randomGame());
            }
        }
    }

    /** Get game name from intent and start it. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();

        String gameName = intent.getStringExtra(GAME_NAME);
        final Class<?> gameClass = chooseGameByName(gameName);

        randomGames = new ArrayList<>();
         if (gameName.equals("10 Words")) {
             previousListName = wordListController.getCurrentWordList();
             wordListController.setCurrentDayList();
             randomGames.add(LetterPuzzleActivity.class);
             randomGames.add(ChooseDefinitionActivity.class);
             randomGames.add(MatchingActivity.class);
             randomGames.add(SynonymsActivity.class);
         } else {
             randomGames.add(gameClass);
         }

        succeedTasks = 0;
        totalTasks = 0;

        runGame(randomGame());

        final Button toMenuButton = findViewById(R.id.to_menu_button);
        toMenuButton.setVisibility(View.INVISIBLE);

        final Button nextWordButton = findViewById(R.id.next_word_button);
        nextWordButton.setOnClickListener(v -> runGame(randomGame()));

        final Button finishGameButton = findViewById(R.id.end_game_button);
        finishGameButton.setOnClickListener(v -> endGame());
    }

    /** Choose random game. */
    private Class<?> randomGame() {
        return randomGames.get(RANDOM.nextInt(randomGames.size()));
    }

    /** Start game activity. */
    private void runGame(@NotNull Class<?> gameClass) {
        Intent intent = new Intent(this, gameClass);
        startActivityForResult(intent, GAME_RESULT_CODE);
    }

    /** End game and show result. */
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
        toMenuButton.setOnClickListener(v -> finish());
    }

    /** Get class of game by it's name. */
    @Nullable
    private Class<?> chooseGameByName(@NotNull String gameName) { //TODO enum for game names
        switch (gameName) {
            case "Letter Puzzle" : return LetterPuzzleActivity.class;
            case "Choose Definition" : return ChooseDefinitionActivity.class;
            case "Word Puzzle" : return WordPuzzleActivity.class;
            case "Matching" : return MatchingActivity.class;
            case "Synonyms" : return SynonymsActivity.class;
        }
        return null;
    }

    /** React on back button pressed in local games. */
    public static void onBackPressed(@NotNull Activity context) {
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

    /** Set previous list as current when finishing. */
    @Override
    public void onStop() {
        if (previousListName != null && isFinishing()) {
            wordListController.setCurrentWordList(previousListName);
        }
        super.onStop();
    }
}
