package ru.hse.android.project.easyenglish.words;

import android.support.annotation.NonNull;

/** Phrase presents a sentence in English and it's translation. */
public class Phrase {
    private final String russian;
    private final String english;

    public Phrase(@NonNull String russian, @NonNull String english) {
     this.russian = russian;
     this.english = english;
    }

    @NonNull
    public String getRussian() {
        return russian;
    }

    @NonNull
    public String getEnglish() {
        return english;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Phrase) {
            Phrase other = (Phrase) obj;
            return russian.equals(other.russian) && english.equals(other.english);
        }
        return false;
    }
}
