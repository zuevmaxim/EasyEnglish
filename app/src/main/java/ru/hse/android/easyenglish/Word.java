package ru.hse.android.easyenglish;

public class Word {
    private String russian;
    private String english;
    private String transcription;

    public Word(String russian, String english, String transcription) {
        this.russian = russian;
        this.english = english;
        this.transcription = transcription;
    }

    public Word(String russian, String english) {
        this(russian, english, "");
    }

    public String getEnglish() {
        return english;
    }

    public String getRussian() {
        return russian;
    }

    public String getTranscription() {
        return transcription;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public void setRussian(String russian) {
        this.russian = russian;
    }

    public void setTranscription(String transcription) {
        this.transcription = transcription;
    }
}
