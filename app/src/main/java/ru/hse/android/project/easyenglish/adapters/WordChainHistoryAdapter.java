package ru.hse.android.project.easyenglish.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.hse.android.project.easyenglish.Pair;
import ru.hse.android.project.easyenglish.R;
import ru.hse.android.project.easyenglish.controllers.TranslateController;

public class WordChainHistoryAdapter extends RecyclerView.Adapter<WordChainHistoryAdapter.ViewHolder> {

    private final List<Pair<String, String>> englishPairs;
    private final List<Pair<String, String>> russianPairs = new ArrayList<>();
    private final List<Pair<Boolean, Boolean>> switchPairs = new ArrayList<>();
    private final LayoutInflater layoutInflater;

    public WordChainHistoryAdapter(Context context, List<Pair<String, String>> words) {
        englishPairs = words;
        for (Pair<String, String> pair : englishPairs) {
            String firstRussian = TranslateController.fastTranslate(pair.getKey(), TranslateController.TranslateDirection.EN_RU);
            if (pair.getKey().equals("-") || firstRussian == null) {
                firstRussian = "-";
            }
            String secondRussian = TranslateController.fastTranslate(pair.getValue(), TranslateController.TranslateDirection.EN_RU);
            if (pair.getValue().equals("-") || secondRussian == null) {
                secondRussian = "-";
            }
            russianPairs.add(new Pair<>(firstRussian, secondRussian));
            switchPairs.add(new Pair<>(true, true));
        }
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
        Pair<String, String> englishPair = englishPairs.get(position);
        Pair<String, String> russianPair = russianPairs.get(position);
        Pair<Boolean, Boolean> switchPair = switchPairs.get(position);
        String firstText = switchPair.getKey() ? englishPair.getKey() : russianPair.getKey();
        String secondText = switchPair.getValue() ? englishPair.getValue() : russianPair.getValue();
        viewHolder.firstText.setText(firstText);
        viewHolder.secondText.setText(secondText);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return englishPairs.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView firstText;
        private final TextView secondText;
        private ViewHolder(View view) {
            super(view);
            firstText = view.findViewById(R.id.first_text);
            firstText.setOnClickListener(v -> {
                int position = getAdapterPosition();
                Pair<Boolean, Boolean> switchPair = switchPairs.get(position);
                switchPair.setKey(!switchPair.getKey());
                switchPairs.set(position, switchPair);
                notifyDataSetChanged();
            });
            secondText = view.findViewById(R.id.second_text);
            secondText.setOnClickListener(v -> {
                int position = getAdapterPosition();
                Pair<Boolean, Boolean> switchPair = switchPairs.get(position);
                switchPair.setValue(!switchPair.getValue());
                switchPairs.set(position, switchPair);
                notifyDataSetChanged();
            });
        }
    }
}
