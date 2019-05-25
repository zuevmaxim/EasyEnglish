package ru.hse.android.project.easyenglish.adapters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import ru.hse.android.project.easyenglish.EditListActivity;
import ru.hse.android.project.easyenglish.R;

public class WordListAdapter extends ArrayAdapter<String> {
    private static final int EDIT_LIST_CODE = 38;
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

        viewHolder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditListActivity.class);
            intent.putExtra("list name", listName);
            ((Activity) context).startActivityForResult(intent, EDIT_LIST_CODE);
        });
        return convertView;
    }

    private class ViewHolder {
        private final Button editButton;
        private final TextView nameView;
        private ViewHolder(View view){
            editButton = view.findViewById(R.id.set_list_button);
            nameView = view.findViewById(R.id.list_name);
        }
    }
}

