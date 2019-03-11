package com.example.mymagicapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button ok = findViewById(R.id.button);
        final EditText textLogin = findViewById(R.id.loginText);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // чтобы вернуть результат
                Intent intent = new Intent();
                intent.putExtra("result", textLogin.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
