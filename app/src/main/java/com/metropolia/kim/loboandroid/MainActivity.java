package com.metropolia.kim.loboandroid;

import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, android.app.LoaderManager.LoaderCallbacks<Cursor> {

    private Intent alertIntent;
    private Intent usersIntent;
    private static final String MYNAME = "LoboAndroid";
    SimpleCursorAdapter myAdapter;
    private String workerName;
    private String workerTitle;


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

        TextView username = (TextView)findViewById(R.id.textUsername);
        TextView title = (TextView)findViewById(R.id.textProfession);
        Intent i = getIntent();
        workerName = i.getStringExtra("workerName");
        workerTitle = i.getStringExtra("workerTitle");
        username.setText(i.getStringExtra("workerName"));
        title.setText(i.getStringExtra("workerTitle"));


        int[] toViews = {R.id.topic, R.id.timeStamp, R.id.message};
        String[] fromColumns = {"playerid", "playername"}; // change this
        myAdapter = new SimpleCursorAdapter(this, R.layout.conversation_list_item,
                null, fromColumns, toViews, 0);

        //getLoaderManager().initLoader(0, null, this);

        ListView lv = (ListView)findViewById(R.id.myListView);
        lv.setAdapter(myAdapter);



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


    //creates the three dots on the up right of the tool bar
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
        usersIntent = new Intent(this, UsersActivity.class);
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

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(MYNAME, "onCreateLoader()");
        return new CursorLoader(this, Uri.parse("content://com.metropolia.kim.lab21/PlayerTable"),
                null, null, null, null);
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
}
