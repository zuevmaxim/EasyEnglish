package com.example.mymagicapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateActivity extends AppCompatActivity {
    // добавим новых разных событий, на которые реагирует android

    /*
    Запуск: create + start + resume
    Выход назад: pause + stop + destroy
    Выход в другое приложение(или блокировка): pause + stop
    Возврат: start + resume
     */

    // выполняется после create, потом идет resume
    @Override
    protected void onStart() {
        super.onStart();
        Log.d("State", "start");
    }

    // стоп
    @Override
    protected void onStop() {
        super.onStop();
        Log.d("State", "stop");
    }

    // уничтожение
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("State", "destroy");
    }

    // пауза
    // нужно, когда поверх проложения запускается что-то другое(уведобление)
    @Override
    protected void onPause() {
        super.onPause();
        Log.d("State", "pause");
    }

    // переход в активный режим
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("State", "resume");
    }

    // создание события
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);

        Log.d("State", "create");
        // тот кто вызвал
        Intent intent = getIntent();
        String action = intent.getAction(); // если вызвать первым способом(new Intent(MainActivity.this, TimeActivity.class)),
        // то тут будет null...Но как передавать между activity какую-нибудь информацию?

        // Сделаем так, чтобы можно было реагировать на причину вызова, чтобы не плодить Activity
        SimpleDateFormat format;
        if (action.equals("ru.spbau.intent.action.showdate")) {
            format = new SimpleDateFormat("dd.MM.yyyy");
        } else {
            format = new SimpleDateFormat("HH:mm:ss");
        }
        TextView textDate = findViewById(R.id.dateText);

        String date = format.format(new Date(System.currentTimeMillis()));
        textDate.setText(date);

        // мы получили сообщение из вне !!!
        /*
            Способ не очень хорош, если нужно передавать очень много информации :)
            в таком случае лучше через БД
         */
        String message = intent.getStringExtra("message");
        Toast.makeText(DateActivity.this, message, Toast.LENGTH_LONG);
    }
}
