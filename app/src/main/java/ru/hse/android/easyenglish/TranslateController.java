package ru.hse.android.easyenglish;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

class TranslateController {
    static String translate(String word, String languagePair) {
        TranslatorTask translatorTask = new TranslatorTask();
        translatorTask.execute(word, languagePair);
        String translation = null;
        try {
            translation = translatorTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return translation;
    }

    static class TranslatorTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String textToBeTranslated = params[0];
            String languagePair = params[1];

            String yandexKey = "trnsl.1.1.20190312T113058Z.7f00768b72b9448a.44608f0910349bb5b3217137b8e605101fa2e17d";
            String yandexUrl = "https://translate.yandex.net/api/v1.5/tr.json/translate"
                    + "?key=" + yandexKey
                    + "&text=" + textToBeTranslated
                    + "&lang=" + languagePair;
            StringBuilder stringBuilder = new StringBuilder();

            try {
                URL yandexTranslateURL = new URL(yandexUrl);
                HttpURLConnection httpJsonConnection = (HttpURLConnection) yandexTranslateURL.openConnection();

                try (InputStream inputStream = httpJsonConnection.getInputStream();
                     BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                    String jsonString;
                    while ((jsonString = bufferedReader.readLine()) != null) {
                        stringBuilder.append(jsonString).append("\n");
                    }
                }

                httpJsonConnection.disconnect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String resultString = stringBuilder.toString().trim();
            resultString = resultString.substring(resultString.indexOf('[') + 1);
            resultString = resultString.substring(0, resultString.indexOf("]"));
            resultString = resultString.substring(resultString.indexOf("\"") + 1);
            resultString = resultString.substring(0,resultString.indexOf("\""));
            return resultString;
        }
    }
}
