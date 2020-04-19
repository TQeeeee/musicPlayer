package com.example.serviceinnotification;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class MusicService extends Service {
    private MediaPlayer mMediaPlayer;
    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.mMediaPlayer = MediaPlayer.create(this,R.raw.music_demo);
        this.mMediaPlayer.start();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.getAction()!=null && intent.getAction().equals("STOP"))
            stopSelf();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mMediaPlayer.stop();
    }
}
