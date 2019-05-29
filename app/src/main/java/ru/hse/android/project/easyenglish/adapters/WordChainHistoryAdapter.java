package ru.hse.android.project.easyenglish.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.hse.android.project.easyenglish.Pair;
import ru.hse.android.project.easyenglish.R;

public class WordChainHistoryAdapter extends RecyclerView.Adapter<WordChainHistoryAdapter.ViewHolder> {

    private List<Pair<String, String>> words;
    private final LayoutInflater layoutInflater;

    public WordChainHistoryAdapter(Context context, List<Pair<String, String>> words) {
        this.words = words;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public WordChainHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.pair_item, parent, false);
        return new WordChainHistoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordChainHistoryAdapter.ViewHolder viewHolder, int position) {
        Pair<String, String> word = words.get(position);
        viewHolder.firstText.setText(word.getKey());
        viewHolder.secondText.setText(word.getValue());
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
        private final TextView firstText;
        private final TextView secondText;
        private ViewHolder(View view) {
            super(view);
            firstText = view.findViewById(R.id.first_text);
            secondText = view.findViewById(R.id.second_text);
        }
    }
}
