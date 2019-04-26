package ru.hse.android.easyenglish;

public class ExtendedWord extends Word {
    private final PartOfSpeech partOfSpeech;

    public ExtendedWord(String russian, String english, String transcription, PartOfSpeech partOfSpeech) {
        super(russian, english, transcription);
        this.partOfSpeech = partOfSpeech;
    }

    public PartOfSpeech getPartOfSpeech() {
        return partOfSpeech;
    }

    public boolean isNoun() {
        return partOfSpeech == PartOfSpeech.NOUN;
    }
}
