package com.metropolia.kim.loboandroid;

import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SimpleCursorAdapter;

/**
 * Created by kimmo on 27/04/2016.
 */
public class AlertsActivity extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<Cursor> {
    private RadioGroup catRadioGroup;
    private RadioGroup recRadioGroup;
    private RadioGroup hisRadioGroup;
    private ListView alertHistoryView;
    private SimpleCursorAdapter adapter;

    private String workerName;
    private String workerTitle;
    private int range =0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Button sendAlert = (Button)findViewById(R.id.sendAlertBtn);
        Button getHistory = (Button)findViewById(R.id.sendAlertBtn);

        catRadioGroup = (RadioGroup) findViewById(R.id.catRadioGroup);
        recRadioGroup = (RadioGroup) findViewById(R.id.recRadioGroup);
        hisRadioGroup = (RadioGroup) findViewById(R.id.hisRadioGroup);
        alertHistoryView = (ListView) findViewById(R.id.alertHistoryView);

        Intent i = getIntent();
        workerName = i.getStringExtra("workerName");
        workerTitle = i.getStringExtra("workerTitle");

        sendAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAlert();
            }
        });

        getHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAlertHistory();
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }
        return true;
    }

    public void sendAlert () {
        //Get input
        int alertCatInput = catRadioGroup.getCheckedRadioButtonId();
        int alertCat = 0;

        switch (alertCatInput) {
            case R.id.catRadio1: //Need assistance
                alertCat = 0;
                break;
            case R.id.catRadio2: //Disturbance
                alertCat = 1;
                break;
            case R.id.catRadio3: //Fire alert
                alertCat = 2;
                break;
            default:
                Log.d("ALERT", "Invalid alertCat -1");
                break;
        }

        int alertRecInput = recRadioGroup.getCheckedRadioButtonId();
        int alertRec = 0;

        switch (alertRecInput) {
            case R.id.recRadio1: //All
                alertRec = 0;
                break;
            case R.id.recRadio2: //Guards
                alertRec = 1;
                break;
            case R.id.recRadio3: //Doctors
                alertRec = 2;
                break;
            default:
                Log.d("ALERT", "Invalid alertRec -1");
                break;
        }

        String alertXml = "<alert><alertCat>" + alertCat + "</alertCat>" +
                "<receiverGroup>" + alertRec + "</receiverGroup>" +
                "<postName>" + workerName + "</postName>" +
                "<postTitle>" + workerTitle + "</postTitle></alert>";

        String[] params = {"resources/Alerts", "alert", alertXml};
        PostTask postTask = new PostTask(this);
        postTask.execute(params);
    }

    public void getAlertHistory () {
        //Get input
        int rangeInput = hisRadioGroup.getCheckedRadioButtonId();

        switch (rangeInput) {
            case R.id.hisRadio1: //5
                range = 0;
                break;
            case R.id.hisRadio2: //10
                range = 1;
                break;
            case R.id.hisRadio3: //All
                range = 2;
                break;
            default:
                Log.d("ALERT", "Invalid history range -1");
                break;
        }

        String[] params = {"resources/Alerthistory/" + range, "alert"};
        NetworkingTask networkTask = new NetworkingTask(this);
        networkTask.execute(params);
        fillData();
    }

    private void fillData() {
        String[] fromColumns = {"topic", "postname", "currenttime", "receivergroup"}; // from which COLUMNS
        int[] toViews = {R.id.alertTopic, R.id.alertSender, R.id.alertTimestamp, R.id.alertReceiverGroup}; // TO which VIEWS

        // initializing the CursorLoader
        getLoaderManager().initLoader(0, null, this);

        // creating and binding binding adapter
        this.adapter = new SimpleCursorAdapter(this, R.layout.list_item_alerts, null, fromColumns, toViews, 0);
        alertHistoryView.setAdapter(this.adapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d("ALERT","onCreateLoader()");
        String[] projection = {"_id", "topic", "postname", "currenttime", "receivergroup"};
        Uri uri = Uri.parse(ChatProvider.URL + "alerts");
        return new CursorLoader(this, uri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d("ALERT","onCreateLoader()");
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
