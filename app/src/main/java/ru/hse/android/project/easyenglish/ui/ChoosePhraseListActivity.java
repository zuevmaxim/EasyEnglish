package ru.hse.android.project.easyenglish.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

import ru.hse.android.project.easyenglish.R;
import ru.hse.android.project.easyenglish.controllers.MainController;
import ru.hse.android.project.easyenglish.controllers.PhrasesController;

/** In this activity player choose theme of phrases which he wants to train in WordPuzzleActivity. */
public class ChoosePhraseListActivity extends AppCompatActivity {

    /** Current chosen theme. */
    private String currentTheme;

    /** Create activity screen and set starter for game. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_phrase_list);
        setUpListView();
        Button startGameButton = findViewById(R.id.start_game_button);
        startGameButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("game name", "Word Puzzle");
            startActivity(intent);
        });
    }

    /** Create view to show list with theme of phrases. */
    private void setUpListView() {
        final PhrasesController phrasesController = MainController.getGameController().getPhrasesController();
        final List<String> themes = phrasesController.getThemesList();

        ListView themesList = findViewById(R.id.theme_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_single_choice, themes);

        themesList.setItemsCanFocus(true);
        themesList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        themesList.setAdapter(adapter);

        currentTheme = phrasesController.getCurrentTheme();
        themesList.setItemChecked(themes.indexOf(currentTheme), true);
        themesList.setOnItemClickListener((adapterView, view, i, l) -> {
            if (!themes.get(i).equals(currentTheme)) {
                currentTheme = themes.get(i);
                phrasesController.setCurrentTheme(currentTheme);
            }
        });
    }
}
