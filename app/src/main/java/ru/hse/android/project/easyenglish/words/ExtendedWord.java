package ru.hse.android.project.easyenglish.words;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a word with extra information: transcription and part of speech.
 * Appears in the wordInfo TranslateController method.
 */
public class ExtendedWord {

    /** List of available parts of speech. */
    private final List<PartOfSpeech> partOfSpeech = new ArrayList<>();

    /** The word itself. */
    private final String word;

    /** Word's transcription. */
    private final String transcription;

    public ExtendedWord(@NonNull String word, @NonNull String transcription, @NonNull List<PartOfSpeech> partOfSpeech) {
        this.word = word;
        this.transcription = transcription;
        this.partOfSpeech.addAll(partOfSpeech);
    }

    @NonNull
    public String getWord() {
        return word;
    }

    @NonNull
    public String getTranscription() {
        return transcription;
    }

    /** Check if word could be a noun. */
    public boolean isNoun() {
        return partOfSpeech.contains(PartOfSpeech.NOUN);
    }
}
