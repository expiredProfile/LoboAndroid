package com.metropolia.kim.loboandroid;

import android.app.LoaderManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Tommi on 4.5.2016.
 */
public class AlertService extends Service {
    private String title;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        title = intent.getStringExtra("title");
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    Log.d("kek", "service");
                    Log.d("service", "." +title);
                    AlertTask alertTask = new AlertTask(AlertService.this, title);
                    alertTask.execute("..");
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
