package ru.hse.android.project.easyenglish.words;

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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Phrase) {
            Phrase other = (Phrase) obj;
            return russian.equals(other.russian) && english.equals(other.english);
        }
        return false;
    }
}
