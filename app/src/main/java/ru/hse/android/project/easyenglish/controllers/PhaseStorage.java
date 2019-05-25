package ru.hse.android.project.easyenglish.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PhaseStorage {
    private List<Phrase> phrases = new ArrayList<>();
    private int i = 0;

    void updateStorage() {
        i = 0;
        phrases = MainController.getGameController().getPhrasesController().getCurrentThemeList();
        Collections.shuffle(phrases);
    }

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
