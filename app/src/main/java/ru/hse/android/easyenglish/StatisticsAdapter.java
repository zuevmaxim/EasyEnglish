package ru.hse.android.easyenglish;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import java.util.List;

public class StatisticsAdapter extends BaseAdapter {

    private Context context;
    private List<Word> words;
    private int layout;
    private LayoutInflater layoutInflater;

    public StatisticsAdapter(Context context, int layout, List<Word> words) {
        this.layout = layout;
        this.context = context;
        this.words = words;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }
    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @Override
    public int getCount() {
        return words.size();
    }

    @Override
    public Object getItem(int position) {
        return words.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null){
            convertView = layoutInflater.inflate(layout, null, true);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        WordFactory wordFactory = MainController.getGameController().getWordFactory();
        Word word = words.get(position);
        viewHolder.russianWordText.setText(word.getRussian());
        viewHolder.englishWordText.setText(word.getEnglish());
        int errorNumber = wordFactory.getWordErrorNumber(word);
        int totalNumber = wordFactory.getWordTotalNumber(word);
        viewHolder.rightScore.setText(String.valueOf(totalNumber - errorNumber));
        viewHolder.wrongScore.setText(String.valueOf(errorNumber));
        return convertView;
    }

    private class ViewHolder {
        private final TextView russianWordText;
        private final TextView englishWordText;
        private final TextView rightScore;
        private final TextView wrongScore;
        private final Button resetButton;
        private ViewHolder(View view){
            russianWordText = view.findViewById(R.id.russian_word_column);
            englishWordText = view.findViewById(R.id.english_word_column);
            rightScore = view.findViewById(R.id.right_score_column);
            wrongScore = view.findViewById(R.id.wrong_score_column);
            resetButton = view.findViewById(R.id.reset_button);
        }
    }
}
