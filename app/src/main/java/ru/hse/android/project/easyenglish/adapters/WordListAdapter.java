package ru.hse.android.project.easyenglish.adapters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import ru.hse.android.project.easyenglish.EditListActivity;
import ru.hse.android.project.easyenglish.R;
import ru.hse.android.project.easyenglish.controllers.MainController;
import ru.hse.android.project.easyenglish.controllers.WordListController;

public class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.ViewHolder> {

private static final int EDIT_LIST_CODE = 38;
    private final LayoutInflater layoutInflater;
    private final List<String> wordListNames;
    private final Context context;

    public WordListAdapter(Context context, List<String> objects) {
        wordListNames = objects;
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @NonNull
    @Override
    public WordListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.list_item, parent, false);
        return new WordListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordListAdapter.ViewHolder viewHolder, int position) {
        final String listName = wordListNames.get(position);

        viewHolder.nameView.setText(listName);
        WordListController controller = MainController.getGameController().getWordListController();

        if (controller.getWordListId(listName) == controller.getRandomWordListId()) {
            viewHolder.editButton.setText(R.string.Update);
            viewHolder.editButton.setOnClickListener(v -> controller.updateRandomWordList());
        } else {
            viewHolder.editButton.setOnClickListener(v -> {
                Intent intent = new Intent(context, EditListActivity.class);
                intent.putExtra("list name", listName);
                ((Activity) context).startActivityForResult(intent, EDIT_LIST_CODE);
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return wordListNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final Button editButton;
        private final TextView nameView;
        private ViewHolder(View view){
            super(view);
            editButton = view.findViewById(R.id.set_list_button);
            nameView = view.findViewById(R.id.list_name);
        }
    }
}

