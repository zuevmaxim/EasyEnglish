package ru.hse.android.project.easyenglish.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.List;

import ru.hse.android.project.easyenglish.controllers.MainController;
import ru.hse.android.project.easyenglish.R;
import ru.hse.android.project.easyenglish.words.Word;
import ru.hse.android.project.easyenglish.words.WordFactory;

public class StatisticsAdapter extends RecyclerView.Adapter<StatisticsAdapter.ViewHolder> {

    private final List<Word> words;
    private final LayoutInflater layoutInflater;
    private final Context context;

    public StatisticsAdapter(Context context, List<Word> words) {
        this.words = words;
        this.context = context;
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
        final Word word = words.get(position);
        viewHolder.russianWordText.setText(word.getRussian());
        viewHolder.englishWordText.setText(word.getEnglish());
        int errorNumber = wordFactory.getWordErrorNumber(word);
        int totalNumber = wordFactory.getWordTotalNumber(word);
        viewHolder.rightScore.setText(String.valueOf(totalNumber - errorNumber));
        viewHolder.wrongScore.setText(String.valueOf(errorNumber));
        viewHolder.menuButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, viewHolder.menuButton);
            popupMenu.inflate(R.menu.statistics_menu);
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.item_reset) {
                    wordFactory.resetStatistics(word);
                    notifyDataSetChanged();
                }
                return false;
            });
            popupMenu.show();
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

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView russianWordText;
        private final TextView englishWordText;
        private final TextView rightScore;
        private final TextView wrongScore;
        private final TextView menuButton;
        private ViewHolder(View view) {
            super(view);
            russianWordText = view.findViewById(R.id.russian_word_column);
            englishWordText = view.findViewById(R.id.english_word_column);
            rightScore = view.findViewById(R.id.right_score_column);
            wrongScore = view.findViewById(R.id.wrong_score_column);
            menuButton = view.findViewById(R.id.option_menu);
        }
    }
}
