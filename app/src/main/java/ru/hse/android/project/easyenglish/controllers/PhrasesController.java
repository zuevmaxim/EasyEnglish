package ru.hse.android.project.easyenglish.controllers;

import android.content.Context;
import android.support.annotation.NonNull;
import java.util.List;

import ru.hse.android.project.easyenglish.controllers.db.PhrasesDB;
import ru.hse.android.project.easyenglish.words.Phrase;

/**
 * Controller for working with phrases database.
 */
public class PhrasesController {

    private String currentTheme;
    private final PhrasesDB phrasesDB;
    private static final String CURRENT_VALUE =  "1";
    private static final String NOT_CURRENT_VALUE =  "0";

    PhrasesController(@NonNull Context context) {
        phrasesDB = new PhrasesDB(context);
        currentTheme = phrasesDB.getThemeWithCurrentValue(CURRENT_VALUE);
    }

    /** Get the name of the current phrases list. */
    @NonNull
    public String getCurrentTheme() {
        return currentTheme;
    }

    /** Set new current theme. And update phrases storage. */
    public void setCurrentTheme(@NonNull String theme) {
        phrasesDB.setThemeCurrentValue(currentTheme, NOT_CURRENT_VALUE);
        phrasesDB.setThemeCurrentValue(theme, CURRENT_VALUE);
        currentTheme = theme;
        MainController.getGameController().getPhraseStorage().updateStorage();
    }

    /** Get a list of phrases from current list. */
    @NonNull
    public List<Phrase> getCurrentThemeList() {
        return phrasesDB.getThemeList(currentTheme);
    }

    /** Get a list of names of phrases lists. */
    @NonNull
    public List<String> getThemesList() {
        return phrasesDB.getThemesList();
    }
}
