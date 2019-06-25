package ru.hse.android.project.easyenglish;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.hse.android.project.easyenglish.words.Word;

/** Card with English, Russian words and translation for viewPager in LearnWordsActivity. */
public class Card extends Fragment {

    /** Store instance variables. */
    private String englishWord;
    private String russianWord;
    private String transcription;

    /** Card constructor for creating fragment with arguments. */
    public static Card newInstance(@NonNull Word word) {
        Card fragmentFirst = new Card();
        Bundle args = new Bundle();
        args.putString("Russian", word.getRussian());
        args.putString("English", word.getEnglish());
        args.putString("Transcription", word.getTranscription());
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    /** Store instance variables based on arguments passed. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        russianWord = getArguments().getString("Russian");
        englishWord = getArguments().getString("English");
        transcription = getArguments().getString("Transcription");
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