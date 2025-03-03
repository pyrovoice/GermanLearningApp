package com.example.germanapp.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.germanapp.R;
import com.example.germanapp.service.TTSService;
import com.example.germanapp.bean.UserDataService;

public class MainMenuActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Button playButton = findViewById(R.id.playButton);
        playButton.setOnClickListener(v -> switchToPlayActivity());
        findViewById(R.id.eraseDataButton).setOnClickListener(v->eraseData());
        findViewById(R.id.addWordButton).setOnClickListener(v -> switchToAddWordActivity());
        startService(new Intent(this, TTSService.class));
    }

    private void switchToPlayActivity(){
        Intent switchActivityIntent = new Intent(this, PlayActivity.class);
        startActivity(switchActivityIntent);
    }

    private void switchToAddWordActivity(){
        Intent switchActivityIntent = new Intent(this, AddWordActivity.class);
        startActivity(switchActivityIntent);
    }

    private void eraseData(){
        UserDataService.getInstance().eraseUserData();
    }
}
