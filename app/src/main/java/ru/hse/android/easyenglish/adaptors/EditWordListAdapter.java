package ru.hse.android.easyenglish.adaptors;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

import ru.hse.android.easyenglish.Pair;
import ru.hse.android.easyenglish.R;
import ru.hse.android.easyenglish.controllers.MainController;
import ru.hse.android.easyenglish.controllers.TranslateController;
import ru.hse.android.easyenglish.exceptions.WrongWordException;
import ru.hse.android.easyenglish.words.Word;

public class EditWordListAdapter extends ArrayAdapter<Pair<Word, EditWordListAdapter.AUTOCHANGES>> {
    private final int layout;
    private final LayoutInflater layoutInflater;
    private final List<Pair<Word, AUTOCHANGES>> words;

    @SuppressWarnings("SameParameterValue")
    public EditWordListAdapter(Context context, int layout, List<Pair<Word, AUTOCHANGES>> words) {
        super(context, layout, words);
        this.words = words;
        this.layout = layout;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return words.size();
    }

    @Override
    public Pair<Word, AUTOCHANGES> getItem(int position) {
        return words.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
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

    private final AtomicInteger pos = new AtomicInteger();

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
       pos.set(position);

        if (convertView == null){
            convertView = layoutInflater.inflate(layout, null, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final Word word = words.get(position).getKey();
        Consumer<AUTOCHANGES> setAutochanges = (v) -> words.set(pos.get(), new Pair<>(word, v));
        Supplier<AUTOCHANGES> getAutochanges = () -> words.get(pos.get()).getValue();

        viewHolder.russianWordText.setHint("Russian");
        viewHolder.russianWordText.addTextChangedListener(new TextWatcher() {
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
        });

        viewHolder.englishWordText.setHint("English");
        viewHolder.englishWordText.addTextChangedListener(new TextWatcher() {
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
        });

        final AUTOCHANGES type = getAutochanges.get();
        setAutochanges.accept(AUTOCHANGES.SET_UP);
        viewHolder.russianWordText.setText(word.getRussian());
        viewHolder.englishWordText.setText(word.getEnglish());
        setAutochanges.accept(type);

        return convertView;
    }

    public void addRow(){
        words.add(new Pair<>(new Word("", ""), AUTOCHANGES.BOTH));
        notifyDataSetChanged();
    }

    private class ViewHolder {
        private final EditText russianWordText;
        private final EditText englishWordText;
        private final TextInputLayout russianWordLayout;
        private final TextInputLayout englishWordLayout;
        private ViewHolder(View view){
            russianWordText = view.findViewById(R.id.russian_word_text);
            englishWordText = view.findViewById(R.id.english_word_text);
            russianWordLayout = view.findViewById(R.id.russian_word_layout);
            englishWordLayout = view.findViewById(R.id.english_word_layout);
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
