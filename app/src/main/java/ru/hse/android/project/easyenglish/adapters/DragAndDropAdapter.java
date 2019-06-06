package ru.hse.android.project.easyenglish.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ru.hse.android.project.easyenglish.R;

public class DragAndDropAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private int resource;
    public DragAndDropAdapter(Context context, List<String> objects,  int resource) {
        super(context, 0, objects);
        this.mContext = context;
        this.resource = resource;
    }

    @Override
    public boolean isEnabled(int position) {
        return super.isEnabled(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        view = LayoutInflater.from(getContext()).inflate(resource, null);

        TextView textView = view.findViewById(R.id.name);
        textView.setText(getItem(position));

        return view;
    }
}
