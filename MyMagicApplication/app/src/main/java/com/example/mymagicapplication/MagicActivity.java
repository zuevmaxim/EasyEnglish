package com.example.mymagicapplication;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class MagicActivity extends AppCompatActivity {

    /*
    Например, скачивание или обращение к базам данных.
     */
    class MyTask
            extends AsyncTask<String, Integer, Integer> { // выполняется в фоне

        @Override
        // нельзя общаться с графическим интерфейсом!!
        protected Integer doInBackground(String... strings) {
            try {
                for (int i = 1; i <= 5; i++) {
                    TimeUnit.SECONDS.sleep(1);
                    publishProgress(i); // переодически вызывает progressUpdate и выдает последнее
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 42;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MagicActivity.this, "Start", Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            Toast.makeText(MagicActivity.this, "Finish", Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Toast.makeText(MagicActivity.this, values[0].toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magic);

        Button button = findViewById(R.id.magicButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyTask task = new MyTask();
                task.execute();
                try {
                    task.get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
