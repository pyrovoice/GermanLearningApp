package com.example.germanapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.germanapp.service.TTSService;
import com.example.germanapp.model.WordPairTracking;
import com.example.germanapp.bean.WordPairTrackingService;
import com.example.germanapp.service.PlayBackgroundService;

import java.util.Locale;
import java.util.Optional;

public class PlayActivity extends Activity {
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
        showNextWord();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TTSService.destroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.println(Log.DEBUG, null, ">>>Play OnPause<<<");
        startService(new Intent(PlayActivity.this, PlayBackgroundService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        stopService(new Intent(PlayActivity.this, PlayBackgroundService.class));
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
        TTSService.ConvertTextToSpeech(article + " " + word, getWordLocale(currentWordPair, true));
        showRevealButton();
    }

    private void revealWord(){
        String article = getArticleString(currentWordPair, false);
        wordHiddenParticle.setText(article);
        String word = getWordString(currentWordPair, false);
        wordHidden.setText(word);
        TTSService.ConvertTextToSpeech(article + " " + word, getWordLocale(currentWordPair, false));
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