package ru.hse.android.project.easyenglish.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import ru.hse.android.project.easyenglish.Pair;
import ru.hse.android.project.easyenglish.R;
import ru.hse.android.project.easyenglish.controllers.MainController;
import ru.hse.android.project.easyenglish.controllers.TranslateController;
import ru.hse.android.project.easyenglish.exceptions.WrongWordException;
import ru.hse.android.project.easyenglish.words.Word;

public class EditWordListAdapter extends RecyclerView.Adapter<EditWordListAdapter.ViewHolder> {
    private final LayoutInflater layoutInflater;
    private final List<Pair<Word, AUTOCHANGES>> words;
    private final HashMap<Integer, ViewHolderHolder> viewHolderHashMap = new HashMap<>();

    @SuppressWarnings("SameParameterValue")
    public EditWordListAdapter(Context context, List<Pair<Word, AUTOCHANGES>> words) {
        this.words = words;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public EditWordListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.editable_word_item, parent, false);
        return new EditWordListAdapter.ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.id = position;
        final Word word = words.get(viewHolder.getId()).getKey();

        if (!viewHolderHashMap.containsKey(viewHolder.getId())) {
            viewHolderHashMap.put(viewHolder.getId(), new ViewHolderHolder());
            viewHolderHashMap.get(viewHolder.getId()).setViewHolder(viewHolder);
            viewHolderHashMap.get(viewHolder.getId()).init();
        }

        viewHolderHashMap.get(viewHolder.getId()).setViewHolder(viewHolder);
        viewHolder.russianWordText.setHint("Russian");
        viewHolder.englishWordText.setHint("English");
        viewHolder.englishWordText.addTextChangedListener(viewHolderHashMap.get(viewHolder.getId()).englishTextWatcer);
        viewHolder.russianWordText.addTextChangedListener(viewHolderHashMap.get(viewHolder.getId()).russianTextWatcer);
        final AUTOCHANGES type =  words.get(viewHolder.getId()).getValue();
        words.set(viewHolder.getId(), new Pair<>(word, AUTOCHANGES.SET_UP));
        viewHolder.russianWordText.setText(word.getRussian());
        viewHolder.englishWordText.setText(word.getEnglish());
        words.set(viewHolder.getId(), new Pair<>(word, type));
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        holder.russianWordText.removeTextChangedListener(viewHolderHashMap.get(holder.getId()).russianTextWatcer);
        holder.englishWordText.removeTextChangedListener(viewHolderHashMap.get(holder.getId()).englishTextWatcer);
        super.onViewRecycled(holder);
    }

    private void setErrorIfWrongSpelling(ViewHolder viewHolder) {
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

    @Override
    public int getItemCount() {
        return words.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private int id;
        private final EditText russianWordText;
        private final EditText englishWordText;
        private final TextInputLayout russianWordLayout;
        private final TextInputLayout englishWordLayout;
        private ViewHolder(View view){
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

    private class ViewHolderHolder {
        private ViewHolder viewHolder;
        private TextWatcher englishTextWatcer;
        private TextWatcher russianTextWatcer;

        private void init() {
            Word word = words.get(viewHolder.getId()).getKey();
            Consumer<AUTOCHANGES> setAutochanges = (v) -> words.set(viewHolder.getId(), new Pair<>(word, v));
            Supplier<AUTOCHANGES> getAutochanges = () -> words.get(viewHolder.getId()).getValue();
            russianTextWatcer = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (getAutochanges.get() == AUTOCHANGES.SET_UP) {
                        return;
                    }
                    word.setRussian(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {
                    switch (getAutochanges.get()) {
                        case SET_UP:
                            setErrorIfWrongSpelling(viewHolder);
                            return;
                        case BOTH:
                            if (!s.toString().isEmpty()) {
                                setAutochanges.accept(AUTOCHANGES.ENGLISH);
                            }
                            break;
                        case NONE:
                            if (s.toString().isEmpty()) {
                                setAutochanges.accept(AUTOCHANGES.RUSSIAN);
                            }
                            break;
                        case ENGLISH:
                            if (s.toString().isEmpty()) {
                                word.setEnglish("");
                                viewHolder.englishWordText.setText(word.getEnglish());
                                setAutochanges.accept(AUTOCHANGES.BOTH);
                            }
                            break;
                        case RUSSIAN:
                            setAutochanges.accept(AUTOCHANGES.NONE);
                            break;
                    }
                    if (getAutochanges.get() == AUTOCHANGES.ENGLISH) {
                        word.setEnglish((TranslateController.translate(s.toString(), "ru-en")));
                        viewHolder.englishWordText.setText(word.getEnglish());
                        setAutochanges.accept(AUTOCHANGES.ENGLISH);
                    }
                    setErrorIfWrongSpelling(viewHolder);
                }
            };
            englishTextWatcer = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (getAutochanges.get() == AUTOCHANGES.SET_UP) {
                        return;
                    }
                    word.setEnglish(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {
                    switch (getAutochanges.get()) {
                        case SET_UP:
                            setErrorIfWrongSpelling(viewHolder);
                            return;
                        case BOTH:
                            if (!s.toString().isEmpty()) {
                                setAutochanges.accept(AUTOCHANGES.RUSSIAN);
                            }
                            break;
                        case NONE:
                            if (s.toString().isEmpty()) {
                                setAutochanges.accept(AUTOCHANGES.ENGLISH);
                            }
                            break;
                        case ENGLISH:
                            setAutochanges.accept(AUTOCHANGES.NONE);
                            break;
                        case RUSSIAN:
                            if (s.toString().isEmpty()) {
                                word.setRussian("");
                                viewHolder.russianWordText.setText(word.getRussian());
                                setAutochanges.accept(AUTOCHANGES.BOTH);
                            }
                            break;
                    }
                    if (getAutochanges.get() == AUTOCHANGES.RUSSIAN) {
                        word.setRussian((TranslateController.translate(s.toString(), "en-ru")));
                        viewHolder.russianWordText.setText(word.getRussian());
                        setAutochanges.accept(AUTOCHANGES.RUSSIAN);
                    }
                    setErrorIfWrongSpelling(viewHolder);
                }
            };
        }

        public void setViewHolder(ViewHolder viewHolder) {
            this.viewHolder = viewHolder;
        }
    }

    public enum AUTOCHANGES {
        RUSSIAN,
        BOTH,
        NONE,
        ENGLISH,
        SET_UP
    }
}
