package com.ak.production.myapplication;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.IOException;

public class NotificationIntentService extends Service {

    private String NOTIFICATION_TITLE = "Notification Sample App";
    private String CONTENT_TEXT = "Expand me to see a detailed message!";
    MediaPlayer player;

    final IBinder notificationBinder = new NotificationBinder();

    public class NotificationBinder extends Binder {
        public  NotificationIntentService getService(){
            return NotificationIntentService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return notificationBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        player = MediaPlayer.create(this, R.raw.jawaanijaaneman);
        player.setVolume(100,100);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sendNotification();
        onHandleIntent(intent);
       // player.start();
        return START_STICKY;
    }



    protected void onHandleIntent(Intent intent) {
        if(null == intent || intent.getAction() == null) return;
        Log.d("notifiIntent", "onHandleIntent: action-"+intent.getAction());
        switch (intent.getAction()) {
            case "left":
                Handler leftHandler = new Handler(Looper.getMainLooper());
                leftHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(player.isPlaying())
                            player.pause();
                        else
                            player.start();
                        Toast.makeText(getBaseContext(), "You clicked the left button", Toast.LENGTH_LONG).show();
                    }
                });
                break;
            case "right":
                Handler rightHandler = new Handler(Looper.getMainLooper());
                rightHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(player.isPlaying())
                            player.pause();
                        else
                            player.start();
                        Toast.makeText(getBaseContext(), "You clicked the right button", Toast.LENGTH_LONG).show();
                    }
                });
                break;
            case "STOP" :
                stopForeground(true);
                if(player.isPlaying()) player.pause();
                stopSelf();
                break;
            case "START" :
                player.start();
                break;
        }
    }


    public void sendNotification() {

        Log.d("notifiIntent", "sendNotification: inside sned notification");

        RemoteViews expandedView = new RemoteViews(getPackageName(), R.layout.layout_expaneded_notification);
        expandedView.setTextViewText(R.id.tv_heading, "dynamic expanded song name");

        Intent leftIntent = new Intent(this, NotificationIntentService.class);
        leftIntent.setAction("left");
        expandedView.setOnClickPendingIntent(R.id.iv_play_pause, PendingIntent.getService(this, 0, leftIntent, PendingIntent.FLAG_UPDATE_CURRENT));


        RemoteViews collapsedView = new RemoteViews(getPackageName(), R.layout.layout_collapsed_notification);
        collapsedView.setTextViewText(R.id.tv_heading, "dynamic collapsed song name");

        Intent rightIntent = new Intent(this, NotificationIntentService.class);
        rightIntent.setAction("right");
        collapsedView.setOnClickPendingIntent(R.id.iv_play_pause, PendingIntent.getService(this, 0, rightIntent, PendingIntent.FLAG_UPDATE_CURRENT));

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel("serviceChannelId", "serviceChannelName", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setSound(null, null);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"serviceChannelId")
                // these are the three things a NotificationCompat.Builder object requires at a minimum
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(NOTIFICATION_TITLE)
                .setContentText(CONTENT_TEXT)
                // notification will be dismissed when tapped
                .setAutoCancel(true)
                // tapping notification will open MainActivity
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0))
                // setting the custom collapsed and expanded views
                .setCustomContentView(collapsedView)
                .setCustomBigContentView(expandedView);
                // setting style to DecoratedCustomViewStyle() is necessary for custom views to display
               // .setStyle(new NotificationCompat.BigPictureStyle());

        // retrieves android.app.NotificationManager

        startForeground(120, builder.build());
    }


    @Override
    public void onDestroy() {
        stopForeground(true);
        if(null != player && player.isPlaying()) player.pause();
        super.onDestroy();
    }
}
