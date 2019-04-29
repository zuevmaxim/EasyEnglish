package ru.hse.android.easyenglish;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class EditWordListAdapter extends BaseAdapter {
    private final int layout;
    private final LayoutInflater layoutInflater;
    private final List<Pair<Word, AUTOCHANGES>> words;

    @SuppressWarnings("SameParameterValue")
    EditWordListAdapter(Context context, int layout, List<Pair<Word, AUTOCHANGES>> words) {
        this.words = words;
        this.layout = layout;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return words.size();
    }

    @Override
    public Object getItem(int position) {
        return words.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null){
            convertView = layoutInflater.inflate(layout, null, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Word word = words.get(position).getKey();
        Consumer<AUTOCHANGES> setAutochanges = (v) -> words.set(position, new Pair<>(word, v));
        Supplier<AUTOCHANGES> getAutochanges = () -> words.get(position).getValue();

        viewHolder.russianWordText.setHint("Russian");
        viewHolder.russianWordText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                word.setRussian(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                switch (getAutochanges.get()) {
                    case BOTH:
                        if (!s.toString().isEmpty()) {
                            setAutochanges.accept(AUTOCHANGES.ENGLISH);
                        } else {
                            throw new AssertionError(); //TODO
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
                word.setEnglish(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                switch (getAutochanges.get()) {
                    case BOTH:
                        if (!s.toString().isEmpty()) {
                            setAutochanges.accept(AUTOCHANGES.RUSSIAN);
                        } else {
                            throw new AssertionError(); //TODO
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
    
    
    enum AUTOCHANGES {
        RUSSIAN,
        BOTH,
        NONE,
        ENGLISH
    }
}
