package ru.hse.android.project.easyenglish.adapters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;

import ru.hse.android.project.easyenglish.EditListActivity;
import ru.hse.android.project.easyenglish.R;
import ru.hse.android.project.easyenglish.controllers.MainController;
import ru.hse.android.project.easyenglish.controllers.WordListController;
import ru.hse.android.project.easyenglish.exceptions.WrongListNameException;

public class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.ViewHolder> {

private static final int EDIT_LIST_CODE = 38;
    private final LayoutInflater layoutInflater;
    private final List<String> wordListNames;
    private final Context context;
    private WordListController wordListController;
    private int lastSelectedPosition;

    public WordListAdapter(Context context, List<String> objects) {
        wordListNames = objects;
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @NonNull
    @Override
    public WordListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        wordListController = MainController.getGameController().getWordListController();
        lastSelectedPosition = wordListNames.indexOf(wordListController.getCurrentWordList());
        View view = layoutInflater.inflate(R.layout.list_item, parent, false);
        return new WordListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordListAdapter.ViewHolder viewHolder, int position) {
        final String listName = wordListNames.get(position);
        viewHolder.nameView.setText(listName);
        WordListController controller = MainController.getGameController().getWordListController();

        if (controller.getWordListId(listName) == controller.getRandomWordListId()
                || controller.getWordListId(listName) == controller.getDayListId()) {
            viewHolder.menuButton.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(context, viewHolder.menuButton);
                popupMenu.inflate(R.menu.update_menu);
                popupMenu.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.item_update) {
                        if (controller.getWordListId(listName) == controller.getRandomWordListId()) {
                            controller.updateRandomWordList();
                        } else {
                            controller.updateDayList();
                        }
                        notifyDataSetChanged();
                    }
                    return false;
                });
                popupMenu.show();
            });
        } else {
            viewHolder.menuButton.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(context, viewHolder.menuButton);
                popupMenu.inflate(R.menu.edit_list_menu);
                popupMenu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.item_edit :
                            Intent intent = new Intent(context, EditListActivity.class);
                            intent.putExtra("list name", listName);
                            ((Activity) context).startActivityForResult(intent, EDIT_LIST_CODE);
                            notifyDataSetChanged();
                            break;
                        case R.id.item_delete :
                            try {
                                MainController.getGameController().getWordListController().deleteWordList(listName);
                                wordListNames.remove(listName);
                                updateCurrentList(wordListNames.indexOf(wordListController.getCurrentWordList()));
                            } catch (WrongListNameException ignored) { } //this word list exists for sure
                            break;
                    }
                    return false;
                });
                popupMenu.show();
            });
        }
        viewHolder.selectionState.setChecked(lastSelectedPosition == position);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return wordListNames.size();
    }

    private void updateCurrentList(int position) {
        lastSelectedPosition = position;
        notifyDataSetChanged();
        wordListController.setCurrentWordList(wordListNames.get(lastSelectedPosition));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView menuButton;
        private final TextView nameView;
        private RadioButton selectionState;

        private ViewHolder(View view){
            super(view);
            menuButton = view.findViewById(R.id.option_menu);
            nameView = view.findViewById(R.id.list_name);
            selectionState = view.findViewById(R.id.radio_button);
            selectionState.setOnClickListener(v -> updateCurrentList(getAdapterPosition()));
            nameView.setOnClickListener(v -> updateCurrentList(getAdapterPosition()));
        }
    }
}

