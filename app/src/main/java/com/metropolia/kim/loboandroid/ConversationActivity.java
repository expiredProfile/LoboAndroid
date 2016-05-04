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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by kimmo on 03/05/2016.
 */
public class ConversationActivity extends AppCompatActivity implements Obsrvr, android.app.LoaderManager.LoaderCallbacks<Cursor> {
    private String conversationid;
    private String topic;
    SimpleCursorAdapter myAdapter;
    private String workerName;
    private ListView lv;
    private WebSocketConnection chatClient;
    private String message; //Websocket message

    @Override
    protected void onDestroy() {
        super.onDestroy();
        chatClient.disconnect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        connectChatSock();

        Toolbar toolbar2 = (Toolbar) findViewById(R.id.bottomToolbar);
        toolbar2.inflateMenu(R.menu.bottom_menu);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar2.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {


            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.send_message){
                    EditText et = (EditText)findViewById(R.id.myEditText);
                    String content = et.getText().toString();
                    String sender = workerName;
                    String cid = conversationid;
                    Log.d("oma","CID: "+cid);
                    if(content.length() == 0){
                        return false;
                    }
                    String XML = "<message>" +
                            "<content>"+content+"</content>" +
                            "<conversationID>"+cid+"</conversationID>" +
                            "<currentTime></currentTime>" +
                            "<postName>"+workerName+"</postName>" +
                            "</message>";
                    PostTask postTask = new PostTask(ConversationActivity.this);
                    String[] params = {"resources/Messages", "message", XML};
                    postTask.execute(params);
                    if (chatClient == null){
                        Log.d("oma","NULL CLIENT");
                    }
                    chatClient.sendTextMessage(cid);
                }
                return false;
            }
        });

        Intent i = getIntent();
        conversationid = i.getStringExtra("conversationid");
        workerName = i.getStringExtra("workerName");
        topic = i.getStringExtra("topic");
        getSupportActionBar().setTitle(topic);
        lv = (ListView)findViewById(R.id.messagelist);
        Log.d("oma", "CID: "+conversationid+" Name: "+workerName+" Topic: "+topic);
        fillData();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }
        return true;
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
        Log.d("oma", "Cursor rows: "+ data.getCount());
        myAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        myAdapter.swapCursor(null);
    }

    private void connectChatSock(){
        Log.d("oma","ConnectChatSock");
        chatClient = new WebSocketConnection();

        //String wsuri = "ws://192.168.0.14:8080/LoboChat/chatend"; //henks hima
       // String wsuri = "ws://192.168.43.109:8080/LoboChat/chatend";
        String wsuri = "ws://192.168.43.9:8080/LoboChat/chatend";

        try {
            chatClient.connect(wsuri, new WebSocketHandler(){
                @Override
                public void onOpen() {
                    Log.d("oma", "Status: Connected to chat");
                }

                @Override
                public void onTextMessage(String payload) {
                    message = payload;
                    if (message.equals(conversationid)){
                        Log.d("oma","HAE VIESTIT"+message);
                        NetworkingTask msgs = new NetworkingTask(ConversationActivity.this);
                        String[] params = {"resources/Messages/" + conversationid, "message"};
                        msgs.register(ConversationActivity.this);
                        msgs.execute(params);
                    }
                }

                @Override
                public void onClose(int code, String reason) {
                    Log.d("oma", "Connection lost:"+reason);

                }
            });
        } catch (WebSocketException e) {
            e.printStackTrace();
            Log.d("oma","FAILED SOCKET");
        }
        /*URI uri;
        try {
            uri = new URI("wss://192.168.0.14:8080/LoboChat/chatend");
        } catch (URISyntaxException e){
            e.printStackTrace();
            return;
        }

        chatClient = new WebSocketClient(uri) {

            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.d("Websocket", "Opened");
            }

            @Override
            public void onMessage(String s) {
                Log.d("Websocket", "MESSAGE");
                message = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (message.equals(conversationid)){
                            Log.d("oma","HAE VIESTIT"+message);
                        }
                    }
                });
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.d("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.d("Websocket", "Error " + e.getMessage());
            }


        };
        chatClient.connect();*/
    }

    @Override
    public void update() {
        getLoaderManager().restartLoader(0,null, this);
    }
}
