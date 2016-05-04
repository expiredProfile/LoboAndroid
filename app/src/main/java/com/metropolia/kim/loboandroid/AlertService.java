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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("kek", "service");
        String title = intent.getStringExtra("title");
        Log.d("service", "." +title);
        AlertTask alertTask = new AlertTask(this, title);
        alertTask.execute("..");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
