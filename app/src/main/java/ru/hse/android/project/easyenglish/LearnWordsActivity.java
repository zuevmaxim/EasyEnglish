package ru.hse.android.project.easyenglish;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

import ru.hse.android.project.easyenglish.controllers.MainController;
import ru.hse.android.project.easyenglish.controllers.WordListController;
import ru.hse.android.project.easyenglish.words.Word;

/** Activity shows cards with words to learn them. */
public class LearnWordsActivity extends FragmentActivity {

    /** List of words to learn. */
    private List<Word> words;

    /** Create activity screen and list of cards. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_words_cards_mode);
        WordListController wordListController = MainController.getGameController().getWordListController();
        TextView listNameText = findViewById(R.id.list_name_text);
        listNameText.setText(wordListController.getCurrentWordList().toUpperCase());
        words = wordListController.getCurrentListWords();
        ViewPager pager = findViewById(R.id.cards_pager);
        PagerAdapter pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
    }

    /** Adapter for CardsViewList */
    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter(@NonNull FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        /** Total number of pages. */
        @Override
        public int getCount() {
            return words.size();
        }

        /** Get the fragment to display for that page. */
        @Override
        @NonNull
        public Fragment getItem(int position) {
            return Card.newInstance(words.get(position));
        }

        /** Get the page title for the top indicator. */
        @Override
        @NonNull
        public CharSequence getPageTitle(int position) {
            return words.get(position).getEnglish();
        }
    }
}