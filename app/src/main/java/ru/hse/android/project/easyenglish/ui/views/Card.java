package ru.hse.android.project.easyenglish.ui.views;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.hse.android.project.easyenglish.R;
import ru.hse.android.project.easyenglish.words.Word;

/** Card with English, Russian words and translation for viewPager in LearnWordsActivity. */
public class Card extends Fragment {

    /** Tags for sending words. */
    private static final String RUSSIAN_TAG = "Russian";
    private static final String ENGLISH_TAG = "English";
    private static final String TRANSCRIPTION_TAG = "Transcription";

    /** Store instance variables. */
    private String englishWord;
    private String russianWord;
    private String transcription;

    /** Card constructor for creating fragment with arguments. */
    public static Card newInstance(@NonNull Word word) {
        Card fragmentFirst = new Card();
        Bundle args = new Bundle();
        args.putString(RUSSIAN_TAG, word.getRussian());
        args.putString(ENGLISH_TAG, word.getEnglish());
        args.putString(TRANSCRIPTION_TAG, word.getTranscription());
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    /** Store instance variables based on arguments passed. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        russianWord = getArguments().getString(RUSSIAN_TAG);
        englishWord = getArguments().getString(ENGLISH_TAG);
        transcription = getArguments().getString(TRANSCRIPTION_TAG);
    }

    /** Inflate the view for the fragment based on layout XML. */
    @Override
    @NonNull
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.card, container, false);
        TextView russianWordText = view.findViewById(R.id.russian_word_text);
        russianWordText.setText(russianWord);
        TextView englishWordText = view.findViewById(R.id.english_word_text);
        englishWordText.setText(englishWord);
        TextView transcriptionText = view.findViewById(R.id.transcription_text);
        transcriptionText.setText(transcription);
        return view;
    }
}