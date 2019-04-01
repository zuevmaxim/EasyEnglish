package ru.hse.android.easyenglish;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list);

        Map<String, List<String>> words = MainController.getGameController().getWordListController(this).getAllWordLists();

        Map<String, String> map;
        ArrayList<Map<String, String>> groupDataList = new ArrayList<>();
        for (String group : words.keySet()) {
            map = new HashMap<>();
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
                map.put("word", word);
                childDataItemList.add(map);
            }
            childDataList.add(childDataItemList);
        }

        String childFrom[] = new String[] { "word" };
        int childTo[] = new int[] { android.R.id.text1 };

        SimpleExpandableListAdapter expandableListAdapter = new SimpleExpandableListAdapter(
                this, groupDataList,
                android.R.layout.simple_expandable_list_item_1, groupFrom,
                groupTo, childDataList, android.R.layout.simple_list_item_1,
                childFrom, childTo);

        ExpandableListView expandableListView = findViewById(R.id.expanded_list);
        expandableListView.setAdapter(expandableListAdapter);
    }
}
