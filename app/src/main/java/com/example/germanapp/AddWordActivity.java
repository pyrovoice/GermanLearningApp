package com.example.germanapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.germanapp.model.PriorityLevel;
import com.example.germanapp.model.UserWordPair;
import com.example.germanapp.model.WordPair;
import com.example.germanapp.service.UserDataService;

import java.util.UUID;

public class AddWordActivity extends Activity {
    EditText englishParticle;
    EditText englishWord;
    EditText germanParticle;
    EditText germanWord;
    Spinner wordPriority;
    UUID wordPairID = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_word_activity);
        englishParticle = findViewById(R.id.engParticle);
        englishWord = findViewById(R.id.englishWord);
        germanParticle = findViewById(R.id.gerParticle);
        germanWord = findViewById(R.id.germanWord);
        addEnterListener();
        wordPriority = findViewById(R.id.wordPrioritySpinner);
        findViewById(R.id.clearButton).setOnClickListener(v->clearEntries());
        findViewById(R.id.saveButton).setOnClickListener(v->upsertWord());
        findViewById(R.id.savedWordsButton).setOnClickListener(v->switchToWordListActivity());
        ArrayAdapter<PriorityLevel> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, PriorityLevel.values());
        wordPriority.setAdapter(adapter);
        wordPriority.setSelection(2);
        

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            UserWordPair userWordPair = (UserWordPair) extras.getSerializable("wordPair");
            if(userWordPair != null){
                setEntries(userWordPair.getWordPair());
                wordPairID = userWordPair.getUuid();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void clearEntries(){
        englishParticle.setText("");
        englishWord.setText("");
        germanParticle.setText("");
        germanWord.setText("");
        wordPriority.setSelection(2);
        wordPairID = null;
    }

    private void setEntries(WordPair wordPair){
        if(wordPair == null){
            return;
        }
        englishParticle.setText(wordPair.getEnglishArticle());
        englishWord.setText(wordPair.getEnglishWord());
        germanParticle.setText(wordPair.getGermanArticle());
        germanWord.setText(wordPair.getGermanWord());
        wordPriority.setSelection(wordPair.getPriorityLevel().ordinal());
    }

    private WordPair getWordPairFromEntries(){
        String engPart = englishParticle.getText().toString();
        String engWord = englishWord.getText().toString();
        String gerPart = germanParticle.getText().toString();
        String gerWord = germanWord.getText().toString();
        PriorityLevel prio = (PriorityLevel) wordPriority.getSelectedItem();
        return new WordPair(engPart, engWord, gerPart, gerWord, prio);
    }

    private void upsertWord(){
        boolean success = UserDataService.getInstance().upsertUserWord(getWordPairFromEntries(), wordPairID);

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(" ");

        if(success){
            alertDialog.setMessage("Success");
        }else{
            alertDialog.setMessage("Failure to Save!");
        }
        alertDialog.show();
        final Handler handler = new Handler();
        handler.postDelayed(alertDialog::dismiss, 400);
    }

    private void switchToWordListActivity(){
        Intent switchActivityIntent = new Intent(this, UserWordListActivity.class);
        startActivity(switchActivityIntent);
    }

    EditText[] order = {englishParticle, englishWord, germanParticle, germanWord};
    private void jumpToNextEntry(EditText currentSelection){
        if(englishParticle.getText().toString().isEmpty()){
            englishParticle.requestFocus();
        }else if(englishWord.getText().toString().isEmpty()){
            englishWord.requestFocus();
        }else if(germanParticle.getText().toString().isEmpty()){
            germanParticle.requestFocus();
        }else if(germanWord.getText().toString().isEmpty()){
            germanWord.requestFocus();
        }
    }
    
    private void addEnterListener(){
        englishParticle.setOnKeyListener((v, keyCode, event) -> {
            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // Perform action on key press
                jumpToNextEntry(englishParticle);
                return true;
            }
            return false;
        });
        englishWord.setOnKeyListener((v, keyCode, event) -> {
            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // Perform action on key press
                Log.println(Log.DEBUG, null, ">>>PRESS<<<");
                jumpToNextEntry(englishWord);
                return true;
            }
            return false;
        });
        englishWord.setImeActionLabel("Custom text", KeyEvent.KEYCODE_ENTER);
        germanParticle.setOnKeyListener((v, keyCode, event) -> {
            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // Perform action on key press
                jumpToNextEntry(germanParticle);
                return true;
            }
            return false;
        });
        germanWord.setOnKeyListener((v, keyCode, event) -> {
            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // Perform action on key press
                jumpToNextEntry(germanWord);
                return true;
            }
            return false;
        });

    }
}
