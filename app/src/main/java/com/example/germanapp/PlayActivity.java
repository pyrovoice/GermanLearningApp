package com.example.germanapp;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.germanapp.model.WordPairTracking;
import com.example.germanapp.service.WordPairTrackingService;

import java.util.Locale;
import java.util.Optional;

public class PlayActivity extends Activity {
    TextToSpeech tts;
    TextView wordShown;
    TextView wordShownParticle;
    TextView wordHidden;
    TextView wordHiddenParticle;
    WordPairTrackingService wordPairTrackingService;
    final Locale engLocal = Locale.US;
    final Locale gerLocal = Locale.GERMANY;
    WordPairTracking currentWordPair = null;
    Button revealButton;
    Button goodAnswerButton;
    Button wrongAnswerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        wordPairTrackingService = WordPairTrackingService.getInstance();

        wordShown = findViewById(R.id.wordShown);
        wordShownParticle = findViewById(R.id.wordShownParticle);
        wordHidden = findViewById(R.id.wordHidden);
        wordHiddenParticle = findViewById(R.id.wordHiddenParticle);
        revealButton = findViewById(R.id.revealButton);
        goodAnswerButton = findViewById(R.id.goodAnswerButton);
        wrongAnswerButton = findViewById(R.id.wrongAnswerButton);

        revealButton.setOnClickListener(v -> revealWord());
        goodAnswerButton.setOnClickListener(v -> isSuccess(true));
        wrongAnswerButton.setOnClickListener(v -> isSuccess(false));
        tts = new TextToSpeech(PlayActivity.this, status -> {
            // TODO Auto-generated method stub
            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(gerLocal);
                if (result == TextToSpeech.LANG_MISSING_DATA ||
                        result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("error", "This Language is not supported");
                }else{
                    showNextWord();
                }
            } else
                Log.e("error", "Initialization Failed!");
        });
    }

    @Override
    protected void onPause() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onPause();
    }

    private void ConvertTextToSpeech(String text, Locale locale) {
        if (tts.getVoice().getLocale() != locale) {
            tts.setLanguage(locale);
        }
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "0");
    }

    private void showNextWord() {
        wordHidden.setText("");
        wordHiddenParticle.setText("");
        Optional<WordPairTracking> nextWordOpt = wordPairTrackingService.getNextWord();
        if(nextWordOpt.isEmpty()){
            Log.println(Log.DEBUG, null, "No more words to show");
            this.finishAffinity();
            return;
        }
        currentWordPair = nextWordOpt.get();
        String article = getArticleString(currentWordPair, true);
        wordShownParticle.setText(article);
        String word = getWordString(currentWordPair, true);
        wordShown.setText(word);
        ConvertTextToSpeech(article + " " + word, getWordLocale(currentWordPair, true));
        showRevealButton();
    }

    private void revealWord(){
        String article = getArticleString(currentWordPair, false);
        wordHiddenParticle.setText(article);
        String word = getWordString(currentWordPair, false);
        wordHidden.setText(word);
        ConvertTextToSpeech(article + " " + word, getWordLocale(currentWordPair, false));
        showAnswerButtons();
    }

    private String getWordString(WordPairTracking currentWordPair, boolean isShown){
        if(currentWordPair.isEnglishShown() == isShown){
            return currentWordPair.getWordPair().getEnglishWord();
        }else{
            return currentWordPair.getWordPair().getGermanWord();
        }
    }

    private String getArticleString(WordPairTracking currentWordPair, boolean isShown){
        if(currentWordPair.isEnglishShown() == isShown){
            return currentWordPair.getWordPair().getEnglishArticle();
        }else{
            return currentWordPair.getWordPair().getGermanArticle();
        }
    }

    private Locale getWordLocale(WordPairTracking currentWordPair, boolean isShown) {
        if(currentWordPair.isEnglishShown() == isShown){
            return engLocal;
        }else{
            return gerLocal;
        }
    }

    private void isSuccess(boolean isSuccess){
        wordPairTrackingService.updateWordPairIncrement(currentWordPair, isSuccess);
        showNextWord();
    }

    private void showRevealButton(){
        hideAllButtons();
        revealButton.setVisibility(View.VISIBLE);
    }
    private void showAnswerButtons(){
        hideAllButtons();
        goodAnswerButton.setVisibility(View.VISIBLE);
        wrongAnswerButton.setVisibility(View.VISIBLE);
    }
    private void hideAllButtons(){
        revealButton.setVisibility(View.GONE);
        goodAnswerButton.setVisibility(View.GONE);
        wrongAnswerButton.setVisibility(View.GONE);
    }
}