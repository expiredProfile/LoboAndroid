package com.metropolia.kim.loboandroid;

import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;

/**
 * Created by kimmo on 27/04/2016.
 */
public class CreateConversationActivity extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<Cursor> {
    private String workerName;
    private ListView lv;
    private SimpleCursorAdapter adapter;
    private CheckedTextView ctv;
    private ArrayList<String> selected = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_conversation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent i = getIntent();
        workerName = i.getStringExtra("workerName");
        lv = (ListView) findViewById(R.id.myListView);
        fillData();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ctv = (CheckedTextView)view.findViewById(R.id.checkName);
                if (ctv.isChecked()){
                    ctv.setChecked(false);
                    selected.remove(ctv.getText().toString());
                } else {
                    ctv.setChecked(true);
                    selected.add(ctv.getText().toString());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return(super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.conversation_done:
                EditText editText = (EditText) findViewById(R.id.editName);
                String topic = editText.getText().toString();
                if (topic.length() < 4) {
                    return false;
                }
                if (selected.isEmpty()) {
                    return false;
                }
                Log.d("oma",workerName);
                String workers = "<group><topic>" + topic + "</topic><workerList><id></id><name>" + workerName + "</name><title></title></workerList>";
                ;
                for (String s : selected) {
                    workers += "<workerList><id></id><name>" + s + "</name><title></title></workerList>";
                }
                workers += "</group>";
                Log.d("oma", workers);
                postConversation(workers);
                finish();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void postConversation(String xml){
        PostTask postTask = new PostTask(this);
        String[] params = {"resources/Conversations", "conversation", xml};
        postTask.execute(params);
    }

    private void fillData(){
        String[] fromColumns = {"name"}; // from which COLUMNS
        int[] toViews = {R.id.checkName}; // TO which VIEWS

        // initializing the CursorLoader
        getLoaderManager().initLoader(0, null, this);

        // creating and binding binding adapter
        this.adapter = new SimpleCursorAdapter(this, R.layout.create_conversation_list_item, null, fromColumns, toViews, 0);
        lv.setAdapter(this.adapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d("oma","onCreateLoader()");
        String[] projection = {"_id", "name"};
        String selection = "name != '"+workerName+"'";
        Uri uri = Uri.parse(ChatProvider.URL + "/workers");
        return new CursorLoader(this, uri, projection, selection, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
