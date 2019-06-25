package ru.hse.android.project.easyenglish.controllers;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.hse.android.project.easyenglish.words.Phrase;

/**
 * PhraseStorage contains current phrases list.
 * Provides an ability to get next phrase in a random order.
 */
public class PhraseStorage {
    /** Current list of phrases. */
    private List<Phrase> phrases = new ArrayList<>();

    /** Index of the last phrase. */
    private int i = 0;

    /** Load new phrases list from a database and shuffle it. */
    void updateStorage() {
        i = 0;
        phrases = MainController.getGameController().getPhrasesController().getCurrentThemeList();
        Collections.shuffle(phrases);
    }

    /** Get next phrase. */
    @NonNull
    public Phrase nextPhrase() {
        Phrase phrase = phrases.get(i);
        i++;
        if (i == phrases.size()) {
            Collections.shuffle(phrases);
            i = 0;
        }
        return phrase;
    }
}
