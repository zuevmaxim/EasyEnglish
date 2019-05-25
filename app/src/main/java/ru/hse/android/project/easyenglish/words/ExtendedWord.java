package ru.hse.android.project.easyenglish.words;

import java.util.ArrayList;
import java.util.List;

public class ExtendedWord {
    private final List<PartOfSpeech> partOfSpeech = new ArrayList<>();
    private String word;
    private String transcription;

    public ExtendedWord(String word, String transcription, List<PartOfSpeech> partOfSpeech) {
        this.word = word;
        this.transcription = transcription;
        this.partOfSpeech.addAll(partOfSpeech);
    }

    public String getWord() {
        return word;
    }

    public String getTranscription() {
        return transcription;
    }

    public boolean isNoun() {
        return partOfSpeech.contains(PartOfSpeech.NOUN);
    }
}
