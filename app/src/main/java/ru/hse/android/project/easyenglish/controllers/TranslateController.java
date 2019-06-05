package ru.hse.android.project.easyenglish.controllers;

import android.os.AsyncTask;

import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.hse.android.project.easyenglish.words.ExtendedWord;
import ru.hse.android.project.easyenglish.words.PartOfSpeech;

public class TranslateController {
    private static final int TIMEOUT = 1000;

    public static String translate(String word, String languagePair) {
        DicResult dicResult = translateTotal(word, languagePair);
        String result = null;
        if (dicResult != null
                && dicResult.def != null
                && dicResult.def.length > 0
                && dicResult.def[0].tr != null
                && dicResult.def[0].tr.length > 0) {
            result = dicResult.def[0].tr[0].text;
        }
        return result;
    }

    public static List<String> getSynonyms(String word) {
        String translation = translate(word, "en-ru");
        return findSynonymsInTranslation(translation, word);
    }

    private static List<String> findSynonymsInTranslation(String russian, String english) {
        DicResult dicResult = translateTotal(russian, "ru-en");
        if (dicResult == null) {
            return null;
        }
        List<String> result = new LinkedList<>();
        if (dicResult.def != null) {
            for (DicResult.Definition definition : dicResult.def) {
                if (definition != null && definition.tr != null) {
                    for (DicResult.Translation translation : definition.tr) {
                        if (translation != null && english.equals(translation.text) && translation.syn != null) {
                            for (DicResult.Synonym synonym : translation.syn) {
                                result.add(synonym.text);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    public static DicResult translateTotal(String word, String languagePair) {
        DictionaryTask translatorTask = new DictionaryTask();
        translatorTask.execute(word, languagePair);
        try {
            return translatorTask.get(TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static PartOfSpeech convertFromString(String pos) {
        if (pos == null) {
            return null;
        }
        switch (pos) {
            case "noun":
                return PartOfSpeech.NOUN;
            case "verb":
                return PartOfSpeech.VERB;
            case "conjunction":
                return PartOfSpeech.CONJUCTION;
            case "adverb":
                return PartOfSpeech.ADVERB;
            case "adjective":
                return PartOfSpeech.ADJECTIVE;
            case "determiner":
                return PartOfSpeech.DETERMINER;
            case "pronoun":
                return PartOfSpeech.PRONOUN;
        }
        return null;
    }

    public static ExtendedWord wordInfo(String word) {
        DicResult dicResult = translateTotal(word, "en-ru");
        String transcription = "";
        List<PartOfSpeech> partOfSpeechList = new ArrayList<>();
        if (dicResult != null
                && dicResult.def != null) {
            if (dicResult.def.length > 0
                    && dicResult.def[0] != null
                    && dicResult.def[0].ts != null) {
                transcription = "[" + dicResult.def[0].ts + "]";
            }
            for (DicResult.Definition definition : dicResult.def) {
                if (definition != null) {
                    PartOfSpeech partOfSpeech = convertFromString(definition.pos);
                    if (partOfSpeech != null) {
                        partOfSpeechList.add(partOfSpeech);
                    }
                }
            }
        }
        return new ExtendedWord(word, transcription, partOfSpeechList);
    }

    static class DictionaryTask extends AsyncTask<String, Void, DicResult> {

        @Override
        protected DicResult doInBackground(String... params) {
            String textToBeTranslated = params[0];
            String languagePair = params[1];
            if (textToBeTranslated == null) {
                return null;
            }
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
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            String resultString = stringBuilder.toString().trim();
            return JSON.parseObject(resultString, DicResult.class);
        }
    }

    public static class DicResult {
        public Definition[] def ;

        public static class Definition {
            public String text;
            public String pos;
            public String ts;
            public String gen;
            public Translation[] tr;
        }

        public static class Translation {
            public String pos;
            public String gen;
            public String text;
            public Synonym[] syn;
            public Meaning[] mean;
            public Example[] ex;
        }

        public static class Synonym {
            public String text;
            public String pos;
            public String gen;
        }

        public static class Meaning {
            public String text;
        }

        public static class Example {
            public String text;
            public Translation[] tr;
        }
    }

    public static String fastTranslate(String word, String languagePair) {
        TranslatorTask translatorTask = new TranslatorTask();
        translatorTask.execute(word, languagePair);
        String result = null;
        try {
            result = translatorTask.get(TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return result;
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
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            String resultString = stringBuilder.toString().trim();
            Pattern pattern = Pattern.compile("\\[\"(\\w*)\"\\]");
            Matcher matcher = pattern.matcher(resultString);
            if (matcher.find()) {
                String result = matcher.group();
                return result.substring(2, result.length() - 2);
            }
            return null;
        }
    }
}