package ru.hse.android.project.easyenglish.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.hse.android.project.easyenglish.R;
import ru.hse.android.project.easyenglish.words.Word;

public class LearnWordsListAdapter extends RecyclerView.Adapter<LearnWordsListAdapter.ViewHolder> {

    private final List<Word> words;
    private final LayoutInflater layoutInflater;

    public LearnWordsListAdapter(Context context, List<Word> words) {
        this.words = words;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public LearnWordsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.learn_word_item, parent, false);
        return new LearnWordsListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LearnWordsListAdapter.ViewHolder viewHolder, int position) {
        Word word = words.get(position);
        viewHolder.russianWordText.setText(word.getRussian());
        viewHolder.englishWordText.setText(word.getEnglish());
        viewHolder.transcriptionText.setText(word.getTranscription());
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView russianWordText;
        private final TextView englishWordText;
        private final TextView transcriptionText;
        private ViewHolder(View view) {
            super(view);
            russianWordText = view.findViewById(R.id.russian_word_text);
            englishWordText = view.findViewById(R.id.english_word_text);
            transcriptionText = view.findViewById(R.id.transcription_text);
        }
    }
}
