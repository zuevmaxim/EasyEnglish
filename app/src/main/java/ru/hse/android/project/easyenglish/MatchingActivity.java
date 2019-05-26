package ru.hse.android.project.easyenglish;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ru.hse.android.project.easyenglish.adapters.DragAndDropAdapter;

public class MatchingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching);


        List<String> list = new ArrayList<String>();
        for(int i=1; i<4; i++){
            list.add("Item " + i);
        }


        ListView listView = findViewById(R.id.matching_list);
        DragAndDropAdapter adapter = new DragAndDropAdapter(this, list);
        listView.setAdapter(adapter);

        DragAndDropListView dragListView = findViewById(R.id.matching_drag_and_drop_list);
        DragAndDropAdapter dragListAdapter = new DragAndDropAdapter(this, list);
        dragListView.setAdapter(dragListAdapter);
    }
}
