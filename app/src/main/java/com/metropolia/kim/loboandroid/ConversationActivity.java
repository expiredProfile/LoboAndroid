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
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.List;

/**
 * Created by kimmo on 03/05/2016.
 */
public class ConversationActivity extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<Cursor> {
    private String conversationid;
    private String topic;
    SimpleCursorAdapter myAdapter;
    private String workerName;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Toolbar toolbar2 = (Toolbar) findViewById(R.id.bottomToolbar);
        toolbar2.inflateMenu(R.menu.bottom_menu);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar2.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.send_message){

                }
                return false;
            }
        });

        Intent i = getIntent();
        conversationid = i.getStringExtra("conversationid");
        workerName = i.getStringExtra("workerName");
        topic = i.getStringExtra("topic");
        lv = (ListView)findViewById(R.id.messagelist);
        Log.d("oma", "CID: "+conversationid+" Name: "+workerName+" Topic: "+topic);
        fillData();
    }

    private void fillData() {
        String[] fromColumns = {"postname", "content", "shorttimestamp"}; // from which COLUMNS
        int[] toViews = {R.id.sender, R.id.content, R.id.stamp}; // TO which VIEWS

        // initializing the CursorLoader
        getLoaderManager().initLoader(0, null, this);

        // creating and binding binding adapter
        this.myAdapter = new SimpleCursorAdapter(this, R.layout.list_item_message, null, fromColumns, toViews, 0);
        lv.setAdapter(this.myAdapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d("oma", "onCreateLoader()");
        String[] projection = {"_id", "conversationid", "postname", "content", "shorttimestamp"};
        String selection = "conversationid = '" + conversationid + "'";
        Uri uri = Uri.parse(ChatProvider.URL + "/messages");
        return new CursorLoader(this, uri, projection, selection, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        myAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        myAdapter.swapCursor(null);
    }
}
