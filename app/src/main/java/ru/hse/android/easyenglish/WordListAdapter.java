package ru.hse.android.easyenglish;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class WordListAdapter extends ArrayAdapter<String> {
    private final int layout;
    private final LayoutInflater layoutInflater;
    private final List<String> wordListNames;
    private final Context context;

    public WordListAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        layout = resource;
        wordListNames = objects;
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null){
            convertView = layoutInflater.inflate(layout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final String listName = wordListNames.get(position);

        viewHolder.nameView.setText(listName);

        viewHolder.setButton.setOnClickListener(v -> Toast.makeText(context, "set button", Toast.LENGTH_LONG).show());
        return convertView;
    }

    private class ViewHolder {
        private final Button setButton;
        private final TextView nameView;
        private ViewHolder(View view){
            setButton = view.findViewById(R.id.set_list_button);
            nameView = view.findViewById(R.id.list_name);
        }
    }
}

