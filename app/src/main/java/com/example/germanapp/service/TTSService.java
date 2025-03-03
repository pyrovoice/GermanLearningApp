package com.example.germanapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import androidx.annotation.Nullable;
import java.util.Locale;

public class TTSService extends Service {
    private static TextToSpeech tts;
    private static boolean isTTSInitialized;
    public TTSService(){

    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        tts = new TextToSpeech(getApplicationContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                isTTSInitialized = true;
            } else
                Log.e("error", "Initialization Failed!");
        });
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static TextToSpeech getTTS(){
        if(!isTTSInitialized){
            return null;
        }
        return tts;
    }

    public static void ConvertTextToSpeech(String text, Locale locale) {
        TextToSpeech tts = getTTS();
        if(tts == null){
            Log.println(Log.DEBUG, null, ">>>Called, not Initialized<<<");
            return;
        }
        Log.println(Log.DEBUG, null, ">>>Called<<<");
        if (tts.getVoice().getLocale() != locale) {
            tts.setLanguage(locale);
        }
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "0");
    }

    public static void destroy(){
        if(getTTS() != null){
            getTTS().stop();
            getTTS().shutdown();
        }
    }
}
