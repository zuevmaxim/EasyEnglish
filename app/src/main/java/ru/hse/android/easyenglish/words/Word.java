package ru.hse.android.easyenglish.words;

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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Word) {
            Word word = (Word) obj;
            return russian.equals(word.russian)
                    && english.equals(word.english)
                    && transcription.equals(word.transcription);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (int) (((long) 17 * russian.hashCode() + 23 * english.hashCode() + 29 * transcription.hashCode()) % Integer.MAX_VALUE);
    }
}
