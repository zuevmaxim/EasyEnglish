package ru.hse.android.easyenglish;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.Toast;

import java.util.List;

public class WordListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<String> wordListNames;

    public WordListAdapter(Context context, List<String> wordListNames) {
        this.context = context;
        this.wordListNames = wordListNames;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return wordListNames.size();
    }

    @Override
    public Object getItem(int position) {
        return wordListNames.get(position);
    }


    public String getListName(int position) {
        return (String) getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.list_item, parent, false);
        }
        final String wordListName = getListName(position);
        final CheckedTextView name = view.findViewById(R.id.checked_text_view);
        name.setText(wordListName);
        if (wordListName.equals(MainController.getGameController().getWordListController(context).getCurrentWordList())) {
            Log.e("checked", "find current word list");
            name.setChecked(true);
        }
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "checked from adapter", Toast.LENGTH_LONG).show();
            }
        });

        Button setButton = view.findViewById(R.id.set_list_button);
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "set button", Toast.LENGTH_LONG).show();
            }
        });
        return view;
    }
}

