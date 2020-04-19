package com.example.serviceinnotification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import static java.lang.System.exit;

public class MainActivity extends AppCompatActivity {
    private static String MAINTAG = MainActivity.class.getSimpleName();
    private NotificationManager mNotificationManager;
    private NotificationChannel mNotificationChannel;
    private int NOTE_ID = 11;
    private String Channel_Name = "MUSIC_CHANNEL";
    private String Channel_ID = "MUSIC_CHANNEL_ONE";
    private String Channel_Description = "MUSIC_PLAYER_CHANNEL";
    private String noteTitle = "Music Play";
    private String noteMessage = "";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();
        Intent musicSvc = new Intent(this,MusicService.class);
        stopService(musicSvc);
        //创建通知频道
        createChannel();
        MusicFragment frag_music = new MusicFragment(this);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.music_container,frag_music).commit();


    }

    @Override
    public void onBackPressed() {
        onStop();
    }

    @Override
    protected void onStop() {
        super.onStop();
        confirmExitRunning();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    public void createChannel(){
        NotificationCompat.Builder builder;
        if(mNotificationManager == null){
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            int importance = NotificationManager.IMPORTANCE_LOW;
            mNotificationChannel = mNotificationManager.getNotificationChannel(Channel_ID);
            if(mNotificationChannel == null){
                mNotificationChannel = new NotificationChannel(Channel_ID,Channel_Name,importance);
                mNotificationChannel.setDescription(Channel_Description);
                mNotificationManager.createNotificationChannel(mNotificationChannel);

            }
        }
    }
    public RemoteViews getRemoteView(){
        RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.notification);
        remoteViews.setTextViewText(R.id.tv_msg,noteMessage);
        //播放按钮打开服务
        Intent intent = new Intent(this,MusicService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this,0,intent,0);
        remoteViews.setOnClickPendingIntent(R.id.btn_play,pendingIntent);
        //停止按钮打开服务
        Intent stopIntent = new Intent(this,MusicService.class);
        stopIntent.setAction("STOP");
        PendingIntent pendingStop = PendingIntent.getService(this,0,stopIntent,0);
        remoteViews.setOnClickPendingIntent(R.id.btn_stop,pendingStop);
        //关闭按钮打开广播
        Intent closeIntent = new Intent(this,CloseNotificationReceiver.class);
        PendingIntent bpi = PendingIntent.getBroadcast(this,0,closeIntent,0);
        remoteViews.setOnClickPendingIntent(R.id.btn_close,bpi);
        //打开App按钮打开MainActivity
        Intent mainIntent = new Intent(getApplicationContext(),MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(),
                0,mainIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.iv_icon,pi);
        return  remoteViews;
    }

    //创建并显示通知
    public void createNotification(){
        Intent intent = new Intent(this,MusicService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getService(this,0,intent,0);
        //由于只有一首曲子，所以歌曲名称固定
        noteMessage = "Playing---music_demo";
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN){
            NotificationCompat.Builder compatbuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(noteTitle)
                    .setContentText(noteMessage)
                    .setAutoCancel(false)
                    .setOngoing(true)
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(pendingIntent);
            if(compatbuilder != null){
                compatbuilder.setContent(getRemoteView());
                mNotificationManager.notify(NOTE_ID,compatbuilder.build());
            }


        }else{
            Notification.Builder notificationBuilder =
                    new Notification.Builder(getApplicationContext(),Channel_ID)
                    .setSmallIcon(R.drawable.music_icon)
                    .setContentTitle(noteTitle)
                    .setContentText(noteMessage)
                    .setAutoCancel(false)
                    .setOngoing(true)
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(pendingIntent);
            if(notificationBuilder != null){
                notificationBuilder.setContent(getRemoteView());
                mNotificationManager.notify(NOTE_ID,notificationBuilder.build());

            }


        }

    }




    //退出程序时询问是否要启动后台运行
    public void confirmExitRunning(){
        new AlertDialog.Builder(MainActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("退出提示")
                .setMessage("要在后台运行吗？")
                .setPositiveButton("后台运行", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        createNotification();
                        Intent musicSvc = new Intent(getApplicationContext(),MusicService.class);
                        stopService(musicSvc);
                        exit(0);
                    }
                })
                .setNegativeButton("直接退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent musicSvc = new Intent(getApplicationContext(),MusicService.class);
                        stopService(musicSvc);
                        exit(0);
                    }
                }).create().show();


    }
    //用于关闭通知和服务的静态内部广播类
    public static class CloseNotificationReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            NotificationManager notificationManager =
                    (NotificationManager)context.getSystemService(
                            Context.NOTIFICATION_SERVICE
                    );
            Log.i(MAINTAG,"关闭Notification");
            notificationManager.cancelAll();
            Intent musicSvc = new Intent(context,MusicService.class);
            context.stopService(musicSvc);
        }
    }








}
