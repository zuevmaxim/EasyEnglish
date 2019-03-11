package com.example.mymagicapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    /*
    Запуск разных activity создает стек состояний(basket)
    android может уничтожить activity, которые давно не использовались,
    а потом использует Bundle savedInstanceState, чтобы восстановиться
    но другие данные не восстановятся!!!
    То же самое при (например) повороте экрана -- сохраняются только данные, сохраненные в xml
    Но!! поля компонент сохраняются(напись сохранится)
     */


    private final static String PREF_FILE = "prefs";
    private final static String PREF_NAME = "userName";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 42) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(MainActivity.this, data.getStringExtra("result"), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this,"kek", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        Попробуем сохранить что-нибудь, чтобы оно не уничтожалось между запусками.
        MODE_PRIVATE -- внутри приложения
         */
        final SharedPreferences sharedPreferences = getSharedPreferences(PREF_FILE, MODE_PRIVATE);
        String name = sharedPreferences.getString(PREF_NAME, "");


        if (name.length() > 0) {
            TextView textView = findViewById(R.id.textView);
            textView.setText(name);
        } else {
            // создаем диалоговое окно
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Hello");
            alert.setMessage("input your name");

            final EditText input = new EditText(this);
            alert.setView(input);
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String name = input.getText().toString();
                    TextView textView = findViewById(R.id.textView);
                    textView.setText(name);
                    SharedPreferences.Editor e = sharedPreferences.edit();
                    e.putString(PREF_NAME, name);
                    e.apply();
                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alert.show();
        }

        Button date = findViewById(R.id.date);
        Button time = findViewById(R.id.time);

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Intent intent = new Intent(MainActivity.this, DateActivity.class);
                startActivity(intent);
                */

                Intent intent = new Intent("ru.spbau.intent.action.showtime");
                startActivity(intent);
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Intent intent = new Intent(MainActivity.this, TimeActivity.class);
                startActivity(intent);
                */
                // есть второй способ открывать новую Activity
                // для этого достаточно указать, что мы хотим, а android выдаст список того, кто умеет это делать
                // например, MainActivity умеет реагировать на  <action android:name="android.intent.action.MAIN" />
                // добавили свои новые варианты
                // все это нужно, чтобы пользователь мог выбрать, как это запустить

                Intent intent = new Intent("ru.spbau.intent.action.showdate");
                intent.putExtra("message", "This is very important message.");
                startActivity(intent);
            }
        });

        /*
        Хотим сделать, чтобы activity что-то возвращал
         */
        Button login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivityForResult(intent, 42); // где результат?
                /*
                Гле происходит выполнение? В другом потоке -- специальный поток, где выполняются Listeners
                =>  получаем результат в другом месте
                нужно переделить метод onActivityResult
                 */
            }
        });

        Button magic = findViewById(R.id.magic);
        magic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MagicActivity.class);
                startActivity(intent);
            }
        });
    }
}
