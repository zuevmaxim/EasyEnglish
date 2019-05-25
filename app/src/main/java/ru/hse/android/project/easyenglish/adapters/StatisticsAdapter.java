package ru.hse.android.project.easyenglish.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.stream.Collectors;

import ru.hse.android.project.easyenglish.Pair;
import ru.hse.android.project.easyenglish.controllers.MainController;
import ru.hse.android.project.easyenglish.R;
import ru.hse.android.project.easyenglish.controllers.WordListController;
import ru.hse.android.project.easyenglish.exceptions.WrongListNameException;
import ru.hse.android.project.easyenglish.exceptions.WrongWordException;
import ru.hse.android.project.easyenglish.words.Word;
import ru.hse.android.project.easyenglish.words.WordFactory;

public class StatisticsAdapter extends RecyclerView.Adapter<StatisticsAdapter.ViewHolder> {

    private List<Word> words;
    private final LayoutInflater layoutInflater;

    public StatisticsAdapter(Context context, List<Word> words) {
        this.words = words;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public StatisticsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.statistics_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatisticsAdapter.ViewHolder viewHolder, int position) {
        WordFactory wordFactory = MainController.getGameController().getWordFactory();
        Word word = words.get(position);
        viewHolder.russianWordText.setText(word.getRussian());
        viewHolder.englishWordText.setText(word.getEnglish());
        int errorNumber = wordFactory.getWordErrorNumber(word);
        int totalNumber = wordFactory.getWordTotalNumber(word);
        viewHolder.rightScore.setText(String.valueOf(totalNumber - errorNumber));
        viewHolder.wrongScore.setText(String.valueOf(errorNumber));
        viewHolder.resetButton.setOnClickListener(v -> {
            wordFactory.resetStatistics(word);
            viewHolder.rightScore.setText(String.valueOf(0));
            viewHolder.wrongScore.setText(String.valueOf(0));
        });
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
        private final TextView rightScore;
        private final TextView wrongScore;
        private final Button resetButton;
        private ViewHolder(View view) {
            super(view);
            russianWordText = view.findViewById(R.id.russian_word_column);
            englishWordText = view.findViewById(R.id.english_word_column);
            rightScore = view.findViewById(R.id.right_score_column);
            wrongScore = view.findViewById(R.id.wrong_score_column);
            resetButton = view.findViewById(R.id.reset_button);
        }
    }
}
