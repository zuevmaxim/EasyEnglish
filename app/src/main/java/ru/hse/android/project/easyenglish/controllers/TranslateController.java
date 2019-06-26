package ru.hse.android.project.easyenglish.controllers;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

import ru.hse.android.project.easyenglish.R;
import ru.hse.android.project.easyenglish.words.ExtendedWord;
import ru.hse.android.project.easyenglish.words.PartOfSpeech;

/**
 * Translate controller provides different translations functions.
 * Uses Yandex.Translate API and Yandex.Dictionary API.
 */
public class TranslateController {

    /** Timeout for requests. All methods could return null if an error occurred or timeout. */
    private static final int TIMEOUT = 1000;

    /** Timeout for synonyms game. It is longer in order to load game. */
    private static final int SYNONYMS_TIMEOUT = 2000;

    /** Yandex.Dictionary key for requests. */
    private static String dictionaryKey;

    /** Yandex.Translate key for requests. */
    private static String translateKey;

    /** A template string for dictionary request. Key, translate direction and text should be specified. */
    private static final String DICTIONARY_REQUEST = "https://dictionary.yandex.net/api/v1/dicservice.json/lookup?key=%s&lang=%s&text=%s";

    /** A template string for translate request. Key, translate direction and text should be specified. */
    private static final String TRANSLATE_REQUEST = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=%s&lang=%s&text=%s";

    static void init(Context context) throws IllegalStateException {
        dictionaryKey = context.getString(R.string.yandex_dictionary_key);
        translateKey = context.getString(R.string.yandex_translate_key);
    }

    /**
     * Simple translation method. Takes first translation from yandex list.
     * @param word word to translate
     * @param languagePair translate direction
     * @param timeout time for request
     * @return translation or null if error occurred
     */
    @Nullable
    private static String translate(@NonNull String word, @NonNull TranslateDirection languagePair, int timeout) {
        DicResult dicResult = translateTotal(word, languagePair, timeout);
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

    /**
     * Get list of synonyms for an English word.
     * This method translate a word into Russian
     * and then finds synonyms of English translation in a Russian request.
     * @param word English word to find synonyms
     * @return list of synonyms
     */
    @Nullable
    public static List<String> getSynonyms(@NonNull String word) {
        String translation = translate(word, TranslateDirection.EN_RU, SYNONYMS_TIMEOUT);
        if (translation == null) {
            return null;
        }
        return findSynonymsInTranslation(translation, word, SYNONYMS_TIMEOUT);
    }

    /**
     * Make a request for Russian word and find synonyms of English word.
     * @return list of synonyms of English word
     */
    @Nullable
    private static List<String> findSynonymsInTranslation(@NonNull String russian, @NonNull String english, int timeout) {
        DicResult dicResult = translateTotal(russian, TranslateDirection.RU_EN, timeout);
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

    /** Get total request for a word. */
    @Nullable
    public static DicResult translateTotal(@NonNull String word, @NonNull TranslateDirection languagePair) {
        return translateTotal(word, languagePair, TIMEOUT);
    }

    /** Get total request for a word. */
    @Nullable
    private static DicResult translateTotal(@NonNull String word, @NonNull TranslateDirection languagePair, int timeout) {
        DictionaryTask translatorTask = new DictionaryTask();
        translatorTask.execute(word, languagePair.value);
        try {
            return translatorTask.get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Convert string into PartOfSpeech enum. */
    @Nullable
    private static PartOfSpeech convertFromString(@Nullable String pos) {
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

    /** Get a list of PartOfSpeech of a word from definitions. */
    @NonNull
    private static List<PartOfSpeech> getPartOfSpeechList(@NonNull DicResult.Definition[] definitions) {
        List<PartOfSpeech> partOfSpeechList = new ArrayList<>();
        for (DicResult.Definition definition : definitions) {
            if (definition != null) {
                PartOfSpeech partOfSpeech = convertFromString(definition.pos);
                if (partOfSpeech != null) {
                    partOfSpeechList.add(partOfSpeech);
                }
            }
        }
        return partOfSpeechList;
    }

    /** Get word transcription and part of speech. */
    @NonNull
    public static ExtendedWord wordInfo(@NonNull String word) {
        DicResult dicResult = translateTotal(word, TranslateDirection.EN_RU);
        String transcription = "";
        List<PartOfSpeech> partOfSpeechList = new ArrayList<>();
        if (dicResult != null && dicResult.def != null) {
            if (dicResult.def.length > 0
                    && dicResult.def[0] != null
                    && dicResult.def[0].ts != null) {
                transcription = "[" + dicResult.def[0].ts + "]";
            }
            partOfSpeechList = getPartOfSpeechList(dicResult.def);
        }
        return new ExtendedWord(word, transcription, partOfSpeechList);
    }

    /**
     * Translate method uses Yandex.Translate API.
     * It is faster than getting the whole word information from Yandex.Dictionary.
     */
    @NonNull
    public static String fastTranslate(@NonNull String word, @NonNull TranslateDirection languagePair) {
        TranslatorTask translatorTask = new TranslatorTask();
        translatorTask.execute(word, languagePair.value);
        String result = null;
        try {
            result = translatorTask.get(TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
        return result == null ? "" : result;
    }

    /** Make yandex request and return resulting string. */
    private static String makeYandexRequest(String request) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        URL yandexTranslateURL = new URL(request);
        HttpURLConnection httpJsonConnection = (HttpURLConnection) yandexTranslateURL.openConnection();
        try (InputStream inputStream = httpJsonConnection.getInputStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String jsonString;
            while ((jsonString = bufferedReader.readLine()) != null) {
                stringBuilder.append(jsonString).append("\n");
            }
        }
        httpJsonConnection.disconnect();
        return stringBuilder.toString().trim();
    }

    /** Task for making request to Yandex.Dictionary. */
    static class DictionaryTask extends AsyncTask<String, Void, DicResult> {

        @Nullable
        @Override
        protected DicResult doInBackground(String... params) {
            String textToBeTranslated = params[0];
            String languagePair = params[1];
            if (textToBeTranslated == null || textToBeTranslated.isEmpty()) {
                return null;
            }
            String yandexUrl = String.format(DICTIONARY_REQUEST, dictionaryKey, languagePair, textToBeTranslated);
            try {
                String resultString = makeYandexRequest(yandexUrl);
                return JSON.parseObject(resultString, DicResult.class);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    /** Task for making request to Yandex.Translate. */
    static class TranslatorTask extends AsyncTask<String, Void, String> {
        @Nullable
        @Override
        protected String doInBackground(String... params) {
            String textToBeTranslated = params[0];
            String languagePair = params[1];
            if (textToBeTranslated.isEmpty()) {
                return "";
            }

            String yandexUrl = String.format(TRANSLATE_REQUEST, translateKey, languagePair, textToBeTranslated);
            String resultString;
            try {
                resultString = makeYandexRequest(yandexUrl);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            Pattern pattern = Pattern.compile("\\[\"(\\w*)\"\\]");
            Matcher matcher = pattern.matcher(resultString);
            if (matcher.find()) {
                String result = matcher.group();
                return result.substring(2, result.length() - 2);
            }
            return null;
        }
    }

    /** Dictionary result as it presented in Yandex.Dictionary answer. */
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

    /** Translate direction. */
    public enum TranslateDirection {
        /** From Russian to English. */
        RU_EN("ru-en"),

        /** From English to Russian. */
        EN_RU("en-ru");

        private final String value;

        TranslateDirection(String value) {
            this.value = value;
        }
    }
}