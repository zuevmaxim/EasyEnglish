package ru.hse.android.project.easyenglish.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import ru.hse.android.project.easyenglish.R;
import ru.hse.android.project.easyenglish.controllers.MainController;
import ru.hse.android.project.easyenglish.controllers.WordListController;
import ru.hse.android.project.easyenglish.ui.games.ChooseDefinitionActivity;
import ru.hse.android.project.easyenglish.ui.games.LetterPuzzleActivity;
import ru.hse.android.project.easyenglish.ui.games.MatchingActivity;
import ru.hse.android.project.easyenglish.ui.games.SynonymsActivity;
import ru.hse.android.project.easyenglish.ui.games.WordPuzzleActivity;

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
    public static final String MESSAGE_TAG = "word";

    /** Tag to put extra end game flag to intent. */
    public static final String END_GAME_TAG = "word";

    /** Tad to put extra game name. */
    public static final String GAME_NAME = "game name";

    /** Tag for window with hints. */
    public static final String HINTS_TAG = "hints";

    /** Tag for window with rules. */
    public static final String RULES_TAG = "rules";

    /** Image with game result. */
    private final ImageView imageView = findViewById(R.id.result);

    /** Text with game result. */
    private final TextView gameResultText = findViewById(R.id.game_result);

    /**
     * Games list to play.
     * Contains several games in case of 10 words game, and only one game otherwise.
     */
    private List<Class<?>> currentGames;

    /** Getting result from a game. */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.e("TAG", "onActivityResult");
        if (requestCode == GAME_RESULT_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    if (data == null) {
                        throw new AssertionError();
                    }
                    boolean endOfGame = data.getBooleanExtra(END_GAME_TAG, false);
                    if (endOfGame) {
                        endGame();
                    } else {
                        final TextView gameMessageText = findViewById(R.id.game_result_text);
                        gameMessageText.setVisibility(View.VISIBLE);
                        gameMessageText.setText(data.getStringExtra(MESSAGE_TAG));

                        boolean result = data.getBooleanExtra(GAME_RESULT_TAG, false);
                        if (result) {
                            gameResultText.setText(getString(R.string.right_answer));
                            gameResultText.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
                            imageView.setImageDrawable(getResources().getDrawable(R.drawable.right, null));
                        } else {
                            gameResultText.setText(getString(R.string.wrong_answer));
                            gameResultText.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                            imageView.setImageDrawable(getResources().getDrawable(R.drawable.wrong, null));
                        }

                        succeedTasks += result ? 1 : 0;
                        totalTasks++;
                    }
                    break;
                case RESULT_REMOVE_SYNONYMS:
                    currentGames.remove(SynonymsActivity.class);
                    if (currentGames.isEmpty()) {
                        new AlertDialog.Builder(this)
                                .setMessage(this.getString(R.string.check_internet_connect))
                                .setCancelable(false)
                                .setNeutralButton(android.R.string.ok, (dialogInterface, i) -> finish())
                                .show();
                        return;
                    }
                    runGame(randomGame());
                    break;
            }
        }
    }

    /** Get game name from intent and start it. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        Game gameName = (Game) intent.getSerializableExtra(GAME_NAME);
        final Class<?> gameClass = chooseGameByName(gameName);

        currentGames = new ArrayList<>();
         if (gameName == Game.TEN_WORDS) {
             previousListName = wordListController.getCurrentWordList();
             wordListController.setCurrentDayList();
             currentGames.addAll(Arrays.asList(
                     LetterPuzzleActivity.class, ChooseDefinitionActivity.class,
                     MatchingActivity.class, SynonymsActivity.class));
         } else {
             currentGames.add(gameClass);
         }

        succeedTasks = 0;
        totalTasks = 0;

        runGame(randomGame());

        findViewById(R.id.to_menu_button).setVisibility(View.INVISIBLE);
        findViewById(R.id.next_word_button).setOnClickListener(v -> runGame(randomGame()));
        findViewById(R.id.end_game_button).setOnClickListener(v -> endGame());
    }

    /** Choose random game. */
    @NonNull
    private Class<?> randomGame() {
        return currentGames.get(RANDOM.nextInt(currentGames.size()));
    }

    /** Start game activity. */
    private void runGame(@NonNull Class<?> gameClass) {
        Intent intent = new Intent(this, gameClass);
        startActivityForResult(intent, GAME_RESULT_CODE);
    }

    /** End game and show result. */
    private void endGame() {
        findViewById(R.id.game_result).setVisibility(View.GONE);
        findViewById(R.id.result).setVisibility(View.GONE);
        findViewById(R.id.next_word_button).setVisibility(View.INVISIBLE);
        findViewById(R.id.end_game_button).setVisibility(View.INVISIBLE);

        final TextView gameResultText = findViewById(R.id.game_result_text);
        gameResultText.setText(("result : " + succeedTasks + " out of " + totalTasks));

        final Button toMenuButton = findViewById(R.id.to_menu_button);
        toMenuButton.setVisibility(View.VISIBLE);
        toMenuButton.setOnClickListener(v -> finish());
    }

    /** Get class of game by it's name. */
    @Nullable
    private Class<?> chooseGameByName(@NonNull Game gameName) {
        switch (gameName) {
            case LETTER_PUZZLE : return LetterPuzzleActivity.class;
            case CHOOSE_DEFINITION : return ChooseDefinitionActivity.class;
            case WORD_PUZZLE : return WordPuzzleActivity.class;
            case  MATCHING : return MatchingActivity.class;
            case SYNONYMS : return SynonymsActivity.class;
        }
        return null;
    }

    /** Init button to show rules. */ //TODO extract from games
    public void initRulesButton() {

    }

    /** Init button to show hints. */
    public void initHintsButton() {

    }

    /** Init button to end game. */
    public void initEndGameButton() {

    }

    /** React on back button pressed in local games. */
    public static void onBackPressed(@NonNull Activity context) {
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

    /** Name of all games. */
    public enum Game {
        LETTER_PUZZLE,
        CHOOSE_DEFINITION,
        WORD_PUZZLE,
        MATCHING,
        SYNONYMS,
        TEN_WORDS
    }
}
