package ru.hse.android.easyenglish.controllers;

import android.os.AsyncTask;

import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class TranslateController {
    public static String translate(String word, String languagePair) {
        DictionaryTask translatorTask = new DictionaryTask();
        translatorTask.execute(word, languagePair);
        DicResult result = null;
        try {
            result = translatorTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }


    static class TranslatorTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String textToBeTranslated = params[0];
            String languagePair = params[1];
            if (textToBeTranslated.isEmpty()) {
                return "";
            }
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

    static class DictionaryTask extends AsyncTask<String, Void, DicResult> {

        @Override
        protected DicResult doInBackground(String... params) {
            String textToBeTranslated = params[0];
            String languagePair = params[1];
            if (textToBeTranslated.isEmpty()) {
                return null;
            }
            String yandexKey = "dict.1.1.20190518T080755Z.a6b5a273366a623b.3a3d0f3248d904cd51a43d31e4d86bb7890822a1";
            String yandexUrl = "https://dictionary.yandex.net/api/v1/dicservice.json/lookup"
                    + "?key=" + yandexKey
                    + "&lang=" + languagePair
                    + "&text=" + textToBeTranslated;
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
            return JSON.parseObject(resultString, DicResult.class);
        }
    }

    private static class DicResult {
        public Definition[] def ;

        public Definition[] getDef() {
            return def;
        }

        public void setDef(Definition[] def) {
            this.def = def;
        }

        public static class Definition {
            public String text;
            public String pos;
            public String ts;
            public String gen;
            public Translation[] tr;

            public String getGen() {
                return gen;
            }

            public String getPos() {
                return pos;
            }

            public String getText() {
                return text;
            }

            public String getTs() {
                return ts;
            }

            public Translation[] getTr() {
                return tr;
            }

            public void setGen(String gen) {
                this.gen = gen;
            }

            public void setPos(String pos) {
                this.pos = pos;
            }

            public void setText(String text) {
                this.text = text;
            }

            public void setTr(Translation[] tr) {
                this.tr = tr;
            }

            public void setTs(String ts) {
                this.ts = ts;
            }
        }

        public static class Translation {
            public String pos;
            public String gen;
            public String text;
            public Synonym[] syn;
            public Meaning[] mean;
            public Example[] ex;

            public String getGen() {
                return gen;
            }

            public String getPos() {
                return pos;
            }

            public String getText() {
                return text;
            }

            public void setGen(String gen) {
                this.gen = gen;
            }

            public void setPos(String pos) {
                this.pos = pos;
            }

            public void setText(String text) {
                this.text = text;
            }

            public Example[] getEx() {
                return ex;
            }

            public Meaning[] getMean() {
                return mean;
            }

            public Synonym[] getSyn() {
                return syn;
            }

            public void setEx(Example[] ex) {
                this.ex = ex;
            }

            public void setMean(Meaning[] mean) {
                this.mean = mean;
            }

            public void setSyn(Synonym[] syn) {
                this.syn = syn;
            }
        }

        public static class Synonym {
            public String text;
            public String pos;
            public String gen;

            public String getGen() {
                return gen;
            }

            public String getPos() {
                return pos;
            }

            public String getText() {
                return text;
            }

            public void setGen(String gen) {
                this.gen = gen;
            }

            public void setPos(String pos) {
                this.pos = pos;
            }

            public void setText(String text) {
                this.text = text;
            }
        }

        public static class Meaning {
            public String text;

            public void setText(String text) {
                this.text = text;
            }

            public String getText() {
                return text;
            }
        }

        public static class Example {
            public String text;
            public Translation tr;

            public Translation getTr() {
                return tr;
            }

            public String getText() {
                return text;
            }

            public void setTr(Translation tr) {
                this.tr = tr;
            }

            public void setText(String text) {
                this.text = text;
            }
        }
    }
}

/*
{"head":{},
        "def":[
                {"text":"укроп",
        "pos":"noun",
        "gen":"м",
        "anm":"неодуш",
        "tr":[
                {"text":"dill",
        "pos":"noun",
        "syn":[
                {"text":"fennel","pos":"noun"}],
        "mean":[
                {"text":"фенхель"}],
        "ex":[
                {"text":"свежий укроп","tr":[{"text":"fresh dill"}]},{"text":"сладкий укроп","tr":[{"text":"sweet fennel"}]}]
                }]}
        ]}
        */
