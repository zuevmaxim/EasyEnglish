package ru.hse.android.easyenglish;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class LetterPuzzleActivity extends AppCompatActivity {

    private String shuffleLetters(String word) {
        List<String> letters = Arrays.asList(word.split(""));
        Collections.shuffle(letters);
        StringBuilder shuffledWord = new StringBuilder();
        for (String letter : letters) {
            shuffledWord.append(letter);
        }
        return shuffledWord.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letter_puzzle);

        Button nextWordButton = findViewById(R.id.next_word);
        Button finishGameButton = findViewById(R.id.finish_game);
        Button checkAnswerButton = findViewById(R.id.check_answer);

        final String word = "cat";
        String shuffledWord = shuffleLetters(word);

        final TextView shuffledWordWindow = findViewById(R.id.shuffled_word);
        shuffledWordWindow.setText(shuffledWord);


        checkAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText answerText = findViewById(R.id.answer);
                String answer = answerText.getText().toString();
                if (answer.equals(word)) {
                    Toast.makeText(LetterPuzzleActivity.this, "Right ;)", Toast.LENGTH_LONG).show();
                    v.setEnabled(false);
                } else {
                    Toast.makeText(LetterPuzzleActivity.this, "Wrong ;(", Toast.LENGTH_LONG).show();
                    v.setEnabled(false);
                }
            }
        });

        nextWordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        finishGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LetterPuzzleActivity.this, ErrorActivity.class);
                startActivity(intent);
            }
        });
    }
}
