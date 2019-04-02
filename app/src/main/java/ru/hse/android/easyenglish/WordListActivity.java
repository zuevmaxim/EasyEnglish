package ru.hse.android.easyenglish;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list);

        final ExpandableListView expandableListView = findViewById(R.id.expanded_list);

        final Context context = this;
        expandableListView.setAdapter(getListAdapter(context));
        Button updateRandomListButton = findViewById(R.id.update_random_list_button);
        updateRandomListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainController.getGameController().getWordListController(context).updateRandomWordList();
                MainController.getGameController().getWordStorage().updateStorage(context);
                expandableListView.setAdapter(getListAdapter(context));
            }
        });
    }

    private SimpleExpandableListAdapter getListAdapter(Context context) {
        WordListController wordListController = MainController.getGameController().getWordListController(context);
        Map<String, List<String>> words = wordListController.getAllWordLists();
        String currentList = wordListController.getCurrentWordList();

        Map<String, String> map;
        ArrayList<Map<String, String>> groupDataList = new ArrayList<>();
        for (String group : words.keySet()) {
            map = new HashMap<>();
            if (group.equals(currentList)){
                group += " * current list";
            }
            map.put("groupName", group);
            groupDataList.add(map);
        }

        String groupFrom[] = new String[] { "groupName" };
        int groupTo[] = new int[] { android.R.id.text1 };
        ArrayList<ArrayList<Map<String, String>>> childDataList = new ArrayList<>();
        ArrayList<Map<String, String>> childDataItemList;
        for (List<String> list : words.values()) {
            childDataItemList = new ArrayList<>();
            for (String word : list) {
                map = new HashMap<>();
                map.put("word", word + " - " + TranslateController.translate(word, "ru-en"));
                childDataItemList.add(map);
            }
            childDataList.add(childDataItemList);
        }

        String childFrom[] = new String[] { "word" };
        int childTo[] = new int[] { android.R.id.text1 };

        return new SimpleExpandableListAdapter(
                this, groupDataList,
                android.R.layout.simple_expandable_list_item_1, groupFrom,
                groupTo, childDataList, android.R.layout.simple_list_item_1,
                childFrom, childTo);
    }
}
