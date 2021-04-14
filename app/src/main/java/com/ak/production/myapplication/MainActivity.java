package com.ak.production.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.widget.ImageViewCompat;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RemoteViews;

public class MainActivity extends AppCompatActivity {


    CardView cvMain;
    ImageView imageViewCompat;
    Button btnStart,btnStop;

    //
    NotificationIntentService notificationIntentService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cvMain = findViewById(R.id.cv_main);
        imageViewCompat = findViewById(R.id.iv_close);
        btnStart = findViewById(R.id.btn_start_service);
        btnStop = findViewById(R.id.btn_stop_service);

        Intent serviceIntent = new Intent(this,NotificationIntentService.class);
        bindService(serviceIntent, connection, BIND_AUTO_CREATE);

        imageViewCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cvMain.setVisibility(View.GONE);
            }
        });



        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(MainActivity.this,NotificationIntentService.class);
                serviceIntent.setAction("START");

                boolean running = isMyServiceRunning(NotificationIntentService.class);
                Log.d("notifiIntent", "onClick: service running or not-"+running);
               // if(running) return;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(serviceIntent);
                } else {
                    startService(serviceIntent);
                }
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent serviceIntent = new Intent(MainActivity.this,NotificationIntentService.class);
                stopService(serviceIntent);*/
                notificationIntentService.onDestroy();
                /*final ActivityManager.RunningServiceInfo serviceInfo = getRunningServiceInfo(NotificationIntentService.class, MainActivity.this);
                if (serviceInfo != null) {
                    android.os.Process.killProcess(serviceInfo.pid); //Stop the running service
                }*/
            }
        });

    }

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            notificationIntentService = ((NotificationIntentService.NotificationBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    public static ActivityManager.RunningServiceInfo getRunningServiceInfo(Class serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return service;
            }
        }
        return null;
    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}