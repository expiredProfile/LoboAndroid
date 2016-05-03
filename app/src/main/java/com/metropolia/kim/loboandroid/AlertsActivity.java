package com.metropolia.kim.loboandroid;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;

/**
 * Created by kimmo on 27/04/2016.
 */
public class AlertsActivity extends AppCompatActivity {
    RadioGroup catRadioGroup;
    RadioGroup recRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        catRadioGroup = (RadioGroup) findViewById(R.id.catRadioGroup);
        recRadioGroup = (RadioGroup) findViewById(R.id.recRadioGroup);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }
        return true;
    }

    public void sendAlert() {
        //Get input
        int alertCat = catRadioGroup.getCheckedRadioButtonId();
        int alertRec = recRadioGroup.getCheckedRadioButtonId();

        String[] params = {"resources/Alerts", "alert"};
        NetworkingTask networkTask = new NetworkingTask(this);
        networkTask.execute(params);
    }
}
