package ru.hse.android.project.easyenglish.controllers;

public class Phrase {
    private String russian;
    private String english;

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
