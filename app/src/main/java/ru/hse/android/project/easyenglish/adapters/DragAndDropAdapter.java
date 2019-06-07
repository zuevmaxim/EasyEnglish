package ru.hse.android.project.easyenglish.adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import ru.hse.android.project.easyenglish.R;

/**
 * DragAndDropAdapter provides the ability to move elements of a list.
 * It is used in Matching and WordPuzzle games.
 */
public class DragAndDropAdapter extends ArrayAdapter<String> {

    private final int resource;

    public DragAndDropAdapter(@NotNull Context context, @NotNull List<String> objects, int resource) {
        super(context, 0, objects);
        this.resource = resource;
    }

    @Override
    public boolean isEnabled(int position) {
        return super.isEnabled(position);
    }

    @SuppressLint("ViewHolder")
    @Override
    @NotNull
    public View getView(int position, @Nullable View convertView, @NotNull ViewGroup parent) {
        View view;
        view = LayoutInflater.from(getContext()).inflate(resource, null);

        TextView textView = view.findViewById(R.id.name);
        textView.setText(getItem(position));

        return view;
    }
}
