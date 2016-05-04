package com.metropolia.kim.loboandroid;

import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity
        implements Obsrvr, NavigationView.OnNavigationItemSelectedListener, android.app.LoaderManager.LoaderCallbacks<Cursor> {

    private Intent alertIntent;
    private Intent usersIntent;
    private static final String MYNAME = "LoboAndroid";
    SimpleCursorAdapter myAdapter;
    private String workerName;
    private String workerTitle;
    private ListView lv;
    private boolean first = true;

    Intent serviceIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateConversationActivity.class);
                intent.putExtra("workerName", workerName);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //TextView username = (TextView)findViewById(R.id.textUsername);
        //TextView title = (TextView)findViewById(R.id.textProfession);
        Intent i = getIntent();
        workerName = i.getStringExtra("workerName");
        workerTitle = i.getStringExtra("workerTitle");

        lv = (ListView) findViewById(R.id.myListView);


        if (first) {
            NetworkingTask nt = new NetworkingTask(this);
            String[] params = {"resources/Conversations/" + workerName, "conversation"};
            nt.register(this);
            nt.execute(params);
        }
        fillData();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView conversationid = (TextView) view.findViewById(R.id.cid);
                TextView topic = (TextView) view.findViewById(R.id.topic);
                Intent intent = new Intent(MainActivity.this, ConversationActivity.class);
                intent.putExtra("conversationid", conversationid.getText().toString());
                intent.putExtra("topic", topic.getText().toString());
                intent.putExtra("workerName", workerName);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Start service
        serviceIntent = new Intent(this, NotificationService.class);
        startService(serviceIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "NotificationService stopping", Toast.LENGTH_SHORT).show();
        stopService(serviceIntent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Intent i = new Intent(this, MessageBackgroundService.class);
        i.putExtra("workerName",workerName);
        startService(i);
    }
//creates the three dots on the up right of the tool bar

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("oma", "onResume");
        Intent stop = new Intent(this, MessageBackgroundService.class);
        stopService(stop);
        NetworkingTask nt = new NetworkingTask(this);
        String[] params = {"resources/Conversations/" + workerName, "conversation"};
        nt.register(this);
        nt.execute(params);
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        alertIntent = new Intent(this, AlertsActivity.class);
        alertIntent.putExtra("workerName",workerName);
        alertIntent.putExtra("workerTitle",workerTitle);

        usersIntent = new Intent(this, UsersActivity.class);
        usersIntent.putExtra("workerName", workerName);
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_users) {
            Log.d("MYNAME", "Clicked on users");
            startActivity(usersIntent);
        } else if (id == R.id.nav_alerts) {
            Log.d("MYNAME", "Clicked on alerts");
            startActivity(alertIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void fillData() {
        String[] fromColumns = {"conversationid", "topic", "lastmessage"}; // from which COLUMNS
        int[] toViews = {R.id.cid, R.id.topic, R.id.message}; // TO which VIEWS

        // initializing the CursorLoader
        getLoaderManager().initLoader(0, null, this);

        // creating and binding binding adapter
        this.myAdapter = new SimpleCursorAdapter(this, R.layout.list_item_conversation, null, fromColumns, toViews, 0);
        lv.setAdapter(this.myAdapter);
    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d("oma", "onCreateLoader()");
        String[] projection = {"_id", "conversationid", "topic", "lastmessage", "workername"};
        String selection = "workername = '" + workerName + "'";
        Uri uri = Uri.parse(ChatProvider.URL + "/conversations/name");
        return new CursorLoader(this, uri, projection, selection, null, null);
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor c) {
        Log.d(MYNAME, "onLoadFinished()");
        myAdapter.swapCursor(c);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        Log.d(MYNAME, "onLoaderReset()");
        myAdapter.swapCursor(null);
    }

    @Override
    public void update() {
        getLoaderManager().restartLoader(0,null, this);
    }
}
