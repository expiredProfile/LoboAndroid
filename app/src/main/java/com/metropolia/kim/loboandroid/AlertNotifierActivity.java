package com.metropolia.kim.loboandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AlertNotifierActivity extends AppCompatActivity {
    int alertId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_notifier);

        Intent i = getIntent();
        alertId = i.getIntExtra("alertId", 0);
        String workerTitle = i.getStringExtra("workerTitle");
    }

    public void makeAlert() {
        String[] params = {"resources/Alerts/" + alertId, "alert"};
        NetworkingTask networkTask = new NetworkingTask(this);
        networkTask.execute(params);
    }
}
