package com.example.germanapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainMenuActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Button playButton = findViewById(R.id.playButton);
        playButton.setOnClickListener(v -> switchToPlayActivity());
    }

    private void switchToPlayActivity(){
        Intent switchActivityIntent = new Intent(this, PlayActivity.class);
        startActivity(switchActivityIntent);
    }
}
