package ru.hse.android.project.easyenglish.words;

import android.support.annotation.NonNull;

/** Word contains information about Russian and English writing and transcription. */
public class Word {
    private String russian;
    private String english;
    private String transcription;

    public Word(@NonNull String russian, @NonNull String english, @NonNull String transcription) {
        this.russian = russian;
        this.english = english;
        this.transcription = transcription;
    }

    public Word(@NonNull String russian, @NonNull String english) {
        this(russian, english, "");
    }

    @NonNull
    public String getEnglish() {
        return english;
    }

    @NonNull
    public String getRussian() {
        return russian;
    }

    @NonNull
    public String getTranscription() {
        return transcription;
    }

    public void setEnglish(@NonNull String english) {
        this.english = english;
    }

    public void setRussian(@NonNull String russian) {
        this.russian = russian;
    }

    public void setTranscription(@NonNull String transcription) {
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
