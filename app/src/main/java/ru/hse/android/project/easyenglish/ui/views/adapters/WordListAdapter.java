package ru.hse.android.project.easyenglish.ui.views.adapters;


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

import ru.hse.android.project.easyenglish.R;
import ru.hse.android.project.easyenglish.controllers.MainController;
import ru.hse.android.project.easyenglish.controllers.WordListController;
import ru.hse.android.project.easyenglish.exceptions.WrongListNameException;
import ru.hse.android.project.easyenglish.ui.EditListActivity;

/** WordListAdapter provides the ability to show list with all word list names, edit/update/delete them and choose current word list. */
public class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.ViewHolder> {

    /** Code used in onActivityResult to return from EditListActivity. */
    private static final int EDIT_LIST_CODE = 38;

    /** Tag to put extra list name to intent. */
    public static final String listNameTag = "list name";

    /** LayoutInflater is used to create a new View (or Layout) object from one of xml layouts. */
    private final LayoutInflater layoutInflater;

    /** List of word list names to show. */
    private final List<String> wordListNames;

    /** Context of activity to show list in. */
    private final Context context;

    /** Position with name of last selected current word list name. */
    private int lastSelectedPosition;

    private final WordListController wordListController = MainController.getGameController().getWordListController();

    /**
     * Constructor.
     * @param context the activity to show list in
     * @param wordListNames list with word list names to show
     */
    public WordListAdapter(@NonNull Context context, @NonNull List<String> wordListNames) {
        this.wordListNames = wordListNames;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    /** Creating new ViewHolder. */
    @NonNull
    @Override
    public WordListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        lastSelectedPosition = wordListNames.indexOf(wordListController.getCurrentWordList());
        View view = layoutInflater.inflate(R.layout.list_item, parent, false);
        return new WordListAdapter.ViewHolder(view);
    }

    /** Show menu for random word list and day list (player can not edit and delete, only can update it). */
    private void showUpdateMenu(@NonNull WordListAdapter.ViewHolder viewHolder, @NonNull String listName) {
        viewHolder.menuButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, viewHolder.menuButton);
            popupMenu.inflate(R.menu.update_menu);
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.item_update) {
                    if (wordListController.getWordListId(listName) == wordListController.getRandomWordListId()) {
                        wordListController.updateRandomWordList();
                    } else {
                        wordListController.updateDayList();
                    }
                    notifyDataSetChanged();
                }
                return false;
            });
            popupMenu.show();
        });
    }

    /** Show menu for custom lists (player can edit and delete them). */
    private void showEditDeleteMenu(@NonNull WordListAdapter.ViewHolder viewHolder, @NonNull String listName) {
        viewHolder.menuButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, viewHolder.menuButton);
            popupMenu.inflate(R.menu.edit_list_menu);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.item_edit :
                        Intent intent = new Intent(context, EditListActivity.class);
                        intent.putExtra(listNameTag, listName);
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

    /** Setting data into the view holder. */
    @Override
    public void onBindViewHolder(@NonNull WordListAdapter.ViewHolder viewHolder, int position) {
        final String listName = wordListNames.get(position);
        viewHolder.nameView.setText(listName);
        WordListController controller = MainController.getGameController().getWordListController();

        if (controller.getWordListId(listName) == controller.getRandomWordListId()
                || controller.getWordListId(listName) == controller.getDayListId()) {
            showUpdateMenu(viewHolder, listName);
        } else {
            showEditDeleteMenu(viewHolder, listName);
        }
        viewHolder.selectionState.setChecked(lastSelectedPosition == position);
    }

    /** Size of the list. */
    @Override
    public int getItemCount() {
        return wordListNames.size();
    }

    /** Update current word list and set last selected position to current word list */
    private void updateCurrentList(int position) {
        lastSelectedPosition = position;
        notifyDataSetChanged();
        wordListController.setCurrentWordList(wordListNames.get(lastSelectedPosition));
    }

    /** Holds the view elements. */
    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView menuButton;
        private final TextView nameView;
        private final RadioButton selectionState;

        /** Set listeners to control selection of current word list */
        private ViewHolder(@NonNull View view){
            super(view);
            menuButton = view.findViewById(R.id.option_menu);
            nameView = view.findViewById(R.id.list_name);
            selectionState = view.findViewById(R.id.radio_button);
            selectionState.setOnClickListener(v -> updateCurrentList(getAdapterPosition()));
            nameView.setOnClickListener(v -> updateCurrentList(getAdapterPosition()));
        }
    }
}

