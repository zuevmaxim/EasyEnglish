package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView x = findViewById(R.id.hello);

        Button y = findViewById(R.id.button);
        y.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                x.setText(getString(R.string.lalala));
                Toast.makeText(MainActivity.this, "blabla", Toast.LENGTH_LONG).show();
                Log.e("kek", "Hooray!");
            }
        });

        Button z = findViewById(R.id.button2);
        z.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ScrollingActivity.class);
                startActivity(intent);

                String u = "http://vk.com";
                //Intent int2 = new Intent(Intent.ACTION_VIEW, )
            }
        });

        final ArrayList<String> list = new ArrayList<>();
        list.add("hey1");
        list.add("hey2");
        list.add("hey3");

        ListView w = findViewById(R.id.l1);
        final ArrayAdapter ad = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        w.setAdapter(ad);
        ad.notifyDataSetChanged();

        w.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //x.setText(???);
            }
        });


        final EditText et = findViewById(R.id.editText);

        Button ok = findViewById(R.id.button3);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.add(et.getText().toString());
                ad.notifyDataSetChanged();
            }
        });
    }
}
