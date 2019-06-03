package ru.hse.android.project.easyenglish;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.hse.android.project.easyenglish.words.Word;

public class Card extends Fragment {
    // Store instance variables
    private String englishWord;
    private String russianWord;
    private String transcription;

    // newInstance constructor for creating fragment with arguments
    public static Card newInstance(Word word) {
        Card fragmentFirst = new Card();
        Bundle args = new Bundle();
        args.putString("russian", word.getRussian());
        args.putString("english", word.getEnglish());
        args.putString("transcription", word.getTranscription());
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        russianWord = getArguments().getString("russian");
        englishWord = getArguments().getString("english");
        transcription = getArguments().getString("transcription");
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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