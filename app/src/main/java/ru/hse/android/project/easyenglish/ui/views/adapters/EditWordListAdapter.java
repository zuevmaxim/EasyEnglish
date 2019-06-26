package ru.hse.android.project.easyenglish.ui.views.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import ru.hse.android.project.easyenglish.R;
import ru.hse.android.project.easyenglish.controllers.MainController;
import ru.hse.android.project.easyenglish.controllers.TranslateController;
import ru.hse.android.project.easyenglish.exceptions.WrongWordException;
import ru.hse.android.project.easyenglish.words.Word;

/**
 * EditWordListAdapter provides the ability to edit list of words with automatic translation.
 * The question would the word be auto-translated is described by AUTO_CHANGES enum.
 */
public class EditWordListAdapter extends RecyclerView.Adapter<EditWordListAdapter.ViewHolder> {

    /** LayoutInflater is used to create a new View (or Layout) object from one of xml layouts. */
    private final LayoutInflater layoutInflater;

    /** List to show. The second element of pair describes the state of auto-translation. */
    private final List<Pair<Word, AUTO_CHANGES>> words;

    /** Each word should have it's own TextWatcher, so they are saved in a HashMap. */
    @SuppressLint("UseSparseArrays")
    private final HashMap<Integer, ViewHolderHolder> viewHolderHashMap = new HashMap<>();

    /**
     * Constructor.
     * @param context the activity to show list in
     * @param words list of words to show
     */
    public EditWordListAdapter(@NonNull Context context, @NonNull List<Pair<Word, AUTO_CHANGES>> words) {
        this.words = words;
        layoutInflater = LayoutInflater.from(context);
    }

    /** Creating new ViewHolder. */
    @NonNull
    @Override
    public EditWordListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.editable_word_item, parent, false);
        return new EditWordListAdapter.ViewHolder(view);
    }

    /** Setting data into the view holder. */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        viewHolder.id = position;
        final Word word = words.get(viewHolder.getId()).first;

        if (!viewHolderHashMap.containsKey(viewHolder.getId())) {
            viewHolderHashMap.put(viewHolder.getId(), new ViewHolderHolder());
            viewHolderHashMap.get(viewHolder.getId()).setViewHolder(viewHolder);
            viewHolderHashMap.get(viewHolder.getId()).init();
        }
        viewHolderHashMap.get(viewHolder.getId()).setViewHolder(viewHolder);
        viewHolder.russianWordText.setHint(R.string.russian_hint_text);
        viewHolder.englishWordText.setHint(R.string.english_hint_text);
        viewHolder.englishWordText.addTextChangedListener(viewHolderHashMap.get(viewHolder.getId()).englishTextWatcher);
        viewHolder.russianWordText.addTextChangedListener(viewHolderHashMap.get(viewHolder.getId()).russianTextWatcher);
        final AUTO_CHANGES type =  words.get(viewHolder.getId()).second;
        words.set(viewHolder.getId(), new Pair<>(word, AUTO_CHANGES.SET_UP));
        viewHolder.russianWordText.setText(word.getRussian());
        viewHolder.englishWordText.setText(word.getEnglish());
        words.set(viewHolder.getId(), new Pair<>(word, type));
    }

    /** If a view is recycled, text watcher should be removed. */
    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        holder.russianWordText.removeTextChangedListener(viewHolderHashMap.get(holder.getId()).russianTextWatcher);
        holder.englishWordText.removeTextChangedListener(viewHolderHashMap.get(holder.getId()).englishTextWatcher);
        super.onViewRecycled(holder);
    }

    /** Check data in the element and show error if is needed. */
    private void setErrorIfWrongSpelling(@NonNull ViewHolder viewHolder) {
        try {
            viewHolder.russianWordLayout.setError(null);
            MainController.getGameController().getWordFactory().checkRussianSpelling(viewHolder.russianWordText.getText().toString());
        } catch (WrongWordException e) {
            viewHolder.russianWordLayout.setError(e.getMessage());
        }
        try {
            viewHolder.englishWordLayout.setError(null);
            MainController.getGameController().getWordFactory().checkEnglishSpelling(viewHolder.englishWordText.getText().toString());
        } catch (WrongWordException e) {
            viewHolder.englishWordLayout.setError(e.getMessage());
        }
    }

    /** Size of the list. */
    @Override
    public int getItemCount() {
        return words.size();
    }

    /** Holds the view elements: edit texts and layouts. */
    class ViewHolder extends RecyclerView.ViewHolder {
        /** Position of the element. */
        private int id;

        /** Russian edit text. */
        private final EditText russianWordText;

        /** English edit text. */
        private final EditText englishWordText;

        /** Russian layout, contains error message. */
        private final TextInputLayout russianWordLayout;

        /** English layout, contains error message. */
        private final TextInputLayout englishWordLayout;

        /** View holder constructor. Find all the elements of the item. */
        private ViewHolder(@NonNull View view){
            super(view);
            russianWordText = view.findViewById(R.id.russian_word_text);
            englishWordText = view.findViewById(R.id.english_word_text);
            russianWordLayout = view.findViewById(R.id.russian_word_layout);
            englishWordLayout = view.findViewById(R.id.english_word_layout);
        }

        public int getId() {
            return id;
        }
    }

    /** ViewHolderHolder holds the view holder and text watchers. */
    private class ViewHolderHolder {

        private ViewHolder viewHolder;
        private TextWatcher englishTextWatcher;
        private TextWatcher russianTextWatcher;

        private Consumer<AUTO_CHANGES> setAutoChanges;
        private Supplier<AUTO_CHANGES> getAutoChanges;
        private  Word word;

        /** Construct watchers if there are none. */
        private void init() {
            word = words.get(viewHolder.getId()).first;
            setAutoChanges = (v) -> words.set(viewHolder.getId(), new Pair<>(word, v));
            getAutoChanges = () -> words.get(viewHolder.getId()).second;

            russianTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (getAutoChanges.get() == AUTO_CHANGES.SET_UP) {
                        return;
                    }
                    word.setRussian(s.toString());
                }
                @Override
                public void afterTextChanged(Editable s) {
                    makeRussianTransition(s);
                }
            };

            englishTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (getAutoChanges.get() == AUTO_CHANGES.SET_UP) {
                        return;
                    }
                    word.setEnglish(s.toString());
                }
                @Override
                public void afterTextChanged(Editable s) {
                    makeEnglishTransition(s);
                }
            };
        }

        /** Control state of russian text after it had been changed. */
        private void makeRussianTransition(Editable s) {
            switch (getAutoChanges.get()) {
                case SET_UP:
                    setErrorIfWrongSpelling(viewHolder);
                    return;
                case BOTH:
                    if (!s.toString().isEmpty()) {
                        setAutoChanges.accept(AUTO_CHANGES.ENGLISH);
                    }
                    break;
                case NONE:
                    if (s.toString().isEmpty()) {
                        setAutoChanges.accept(AUTO_CHANGES.RUSSIAN);
                    }
                    break;
                case ENGLISH:
                    if (s.toString().isEmpty()) {
                        word.setEnglish("");
                        viewHolder.englishWordText.setText(word.getEnglish());
                        setAutoChanges.accept(AUTO_CHANGES.BOTH);
                    }
                    break;
                case RUSSIAN:
                    setAutoChanges.accept(AUTO_CHANGES.NONE);
                    break;
            }
            if (getAutoChanges.get() == AUTO_CHANGES.ENGLISH) {
                word.setEnglish((TranslateController.fastTranslate(s.toString(), TranslateController.TranslateDirection.RU_EN)));
                viewHolder.englishWordText.setText(word.getEnglish());
                setAutoChanges.accept(AUTO_CHANGES.ENGLISH);
            }
            setErrorIfWrongSpelling(viewHolder);
        }

        /** Control state of english text after it had been changed. */
        private void makeEnglishTransition(Editable s) {
            switch (getAutoChanges.get()) {
                case SET_UP:
                    setErrorIfWrongSpelling(viewHolder);
                    return;
                case BOTH:
                    if (!s.toString().isEmpty()) {
                        setAutoChanges.accept(AUTO_CHANGES.RUSSIAN);
                    }
                    break;
                case NONE:
                    if (s.toString().isEmpty()) {
                        setAutoChanges.accept(AUTO_CHANGES.ENGLISH);
                    }
                    break;
                case ENGLISH:
                    setAutoChanges.accept(AUTO_CHANGES.NONE);
                    break;
                case RUSSIAN:
                    if (s.toString().isEmpty()) {
                        word.setRussian("");
                        viewHolder.russianWordText.setText(word.getRussian());
                        setAutoChanges.accept(AUTO_CHANGES.BOTH);
                    }
                    break;
            }
            if (getAutoChanges.get() == AUTO_CHANGES.RUSSIAN) {
                word.setRussian((TranslateController.fastTranslate(s.toString(), TranslateController.TranslateDirection.EN_RU)));
                viewHolder.russianWordText.setText(word.getRussian());
                setAutoChanges.accept(AUTO_CHANGES.RUSSIAN);
            }
            setErrorIfWrongSpelling(viewHolder);
        }

        private void setViewHolder(@NonNull ViewHolder viewHolder) {
            this.viewHolder = viewHolder;
        }
    }

    /**
     * This enum describes the state of ability to auto-translate.
     * Transitions:
     * on Russian test changed:
     *      BOTH    -> ENGLISH
     *      ENGLISH -> BOTH    (only if Russian text is empty)
     *      RUSSIAN -> NONE
     *      NONE    -> RUSSIAN (only if Russian text is empty)
     *
     * on English test changed:
     *      BOTH    -> RUSSIAN
     *      RUSSIAN -> BOTH    (only if English text is empty)
     *      ENGLISH -> NONE
     *      NONE    -> ENGLISH (only if English text is empty)
     */
    public enum AUTO_CHANGES {
        /** Only Russian should be translated. It is used when user changes only English text. */
        RUSSIAN,

        /** Both elements can be translated. It is used when item is empty. */
        BOTH,

        /** None of the elements can be translated. It is used when user changed both texts. */
        NONE,

        /** Only English should be translated. It is used when user changes only Russian text. */
        ENGLISH,

        /**
         * None of the elements can be translated and no transitions between states are provided.
         * It is used for programmatically set the text.
         */
        SET_UP
    }
}
