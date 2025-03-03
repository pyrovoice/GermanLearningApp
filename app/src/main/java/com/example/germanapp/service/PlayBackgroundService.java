package com.example.germanapp.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.germanapp.R;
import com.example.germanapp.bean.WordPairTrackingService;
import com.example.germanapp.model.WordPair;
import com.example.germanapp.model.WordPairTracking;

import java.util.Locale;

public class PlayBackgroundService extends Service {
    private final int DELAY_MS = 5000;
    final Handler handler = new Handler();
    private boolean isRunning = false;

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                0);
        thread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isRunning = true;
        playNextWord();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        Toast.makeText(this, "Background running stop", Toast.LENGTH_SHORT).show();
    }

    private void playNextWord(){
        if(!isRunning){
            return;
        }
        if(TTSService.getTTS() == null){
            final Handler handler = new Handler();
            handler.postDelayed(this::playNextWord, DELAY_MS);
            return;
        }

        WordPairTracking currentWord = WordPairTrackingService.getInstance().getNextWord().orElse(null);
        if(currentWord == null){
            return;
        }
        WordPair wordPair = currentWord.getWordPair();
        TTSService.ConvertTextToSpeech(wordPair.getEnglishArticle() + " " + wordPair.getEnglishWord(), Locale.ENGLISH);
        handler.postDelayed(() -> {
            TTSService.ConvertTextToSpeech(wordPair.getGermanArticle() + " " + wordPair.getGermanWord(), Locale.GERMANY);
            handler.postDelayed(this::playNextWord, DELAY_MS);
        }, DELAY_MS);
    }

    /*
    public void setNotification(){
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager notificationManager = (NotificationManager) getSystemService(ns);

        Notification notification = new Notification(R.drawable.app_icon_your_company, null, System.currentTimeMillis());

        RemoteViews notificationView = new RemoteViews(getPackageName(), R.layout.notification_view);
        notificationView.setImageViewResource(0, R.drawable.movie);
        notificationView.setTextViewText(R.id.textView1, songName);

        //the intent that is started when the notification is clicked (works)
        Intent notificationIntent = new Intent(this, PlayerActivity.class);
        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        notification.contentView = notificationView;
        notification.contentIntent = pendingNotificationIntent;

        Intent switchIntent = new Intent("com.example.test.ACTION_PLAY");
        PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(this, 0, switchIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationView.setOnClickPendingIntent(R.id.play_pause, pendingSwitchIntent);
        notificationManager.notify(1, notification);
    }
     */
}
