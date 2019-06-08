package ru.hse.android.project.easyenglish.words;

/** Phrase presents a sentence in English and it's translation. */
public class Phrase {
    private final String russian;
    private final String english;

    public Phrase(String russian, String english) {
     this.russian = russian;
     this.english = english;
    }

    public String getRussian() {
        return russian;
    }

    public String getEnglish() {
        return english;
    }
}
