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
    TextView wordHidden;
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
        wordHidden = findViewById(R.id.wordHidden);
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
                Log.e("error", "Initilization Failed!");
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
        Optional<WordPairTracking> nextWordOpt = wordPairTrackingService.getNextWord();
        if(nextWordOpt.isEmpty()){
            this.finishAffinity();
            return;
        }
        currentWordPair = nextWordOpt.get();
        wordShown.setText(currentWordPair.getWordPair().getEnglishWord());
        ConvertTextToSpeech(currentWordPair.getWordPair().getEnglishWord(), engLocal);
        showRevealButton();
    }

    private void revealWord(){
        wordHidden.setText(currentWordPair.getWordPair().getGermanWord());
        ConvertTextToSpeech(currentWordPair.getWordPair().getGermanWord(), gerLocal);
        showAnswerButtons();
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