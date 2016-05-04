package com.metropolia.kim.loboandroid;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.metropolia.kim.loboandroiddata.Conversation;
import com.metropolia.kim.loboandroiddata.Message;
import com.metropolia.kim.xmlparser.ConversationXmlParser;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

/**
 * Created by Hege on 04/05/2016.
 */

public class MessageBackgroundService extends Service implements Obsrvr {
    private String workerName;
    private List<Conversation> conversations;
    private ArrayList<String> cids;
    private String baseurl = "http://192.168.43.9:8080/LoboChat/";// kim
    //private String baseurl = "http://192.168.43.109:8080/LoboChat/"; //Henks
    //private String baseurl = "http://192.168.0.14:8080/LoboChat/"; //Henks hima
    private WebSocketConnection chatClient;
    private String message;
    private boolean cont = false;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void connectToSocket(){
        Log.d("oma","ConnectChatSock service");
        chatClient = new WebSocketConnection();

        //String wsuri = "ws://192.168.0.14:8080/LoboChat/chatend"; //henks hima
        String wsuri = "ws://192.168.43.109:8080/LoboChat/chatend";

        try {
            chatClient.connect(wsuri, new WebSocketHandler(){
                @Override
                public void onOpen() {
                    Log.d("oma", "Status: Connected to chat");
                }

                @Override
                public void onTextMessage(String payload) {
                    message = payload;
                    if (cids.contains(message)){
                        Log.d("oma","HAE VIESTIT"+message);
                        Conversation update = null;
                        for (Conversation c : conversations){
                            if (Integer.toString(c.getID()).equals(message)){
                                update = c;
                            }
                        }
                        NetworkingTask msgs = new NetworkingTask(MessageBackgroundService.this);
                        String[] params = {"resources/Messages/" + message, "message"};
                        msgs.register(MessageBackgroundService.this);
                        msgs.execute(params);

                        Message latest = update.getMessages().get(update.getMessages().size()-1);
                        makeNotification(update.getTopic(), update.getID(),latest.getShortTime()+" "+latest.getPostName()+": "+latest.getContent());
                    }
                }

                @Override
                public void onClose(int code, String reason) {
                    Log.d("oma", "Connection lost:"+reason);

                }
            });
        } catch (WebSocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        workerName = intent.getStringExtra("workerName");
        connectToSocket();
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean on = true;
                while(on) {
                    HttpURLConnection httpURLConnection;
                    try {
                        URL url = new URL(baseurl + "resources/Conversations/" + workerName);
                        httpURLConnection = (HttpURLConnection) url.openConnection();
                        httpURLConnection.setConnectTimeout(20000);
                        httpURLConnection.setReadTimeout(20000);
                        httpURLConnection.setDoInput(true);
                        httpURLConnection.setRequestMethod("GET");
                        //httpURLConnection.setRequestProperty("Content-Type","application/xml");

                        httpURLConnection.connect();
                        InputStream is = httpURLConnection.getInputStream();

                        ConversationXmlParser xmlParser = new ConversationXmlParser();
                        List<Conversation> parseconversations = xmlParser.parse(is);
                        MessageBackgroundService.this.setConversations(parseconversations);
                    } catch (Exception e) {

                    }
                    try {
                        Thread.sleep(15000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * Used to name the worker thread, important only for debugging.
     */
    public MessageBackgroundService(){

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public void makeNotification(String topic, int cid, String msg) {
        //Service notification
        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_sms_white_24dp)
                .setAutoCancel(true)
                .setContentTitle("New message in "+topic)
                .setContentText(msg)
                .setAutoCancel(true);
        int NOTIF_ID = cid; //Sth here !

        Intent notifIntent = new Intent(this, ConversationActivity.class);
        notifIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notifIntent.putExtra("workername",workerName);
        notifIntent.putExtra("conversationid",Integer.toString(cid));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notifIntent, 0);
        notifBuilder.setContentIntent(pendingIntent);

        NotificationManager notifMan = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notifMan.notify(NOTIF_ID, notifBuilder.build());
    }

    private void setConversations(List<Conversation> ac){
        Log.d("oma", "setConverastions()");
        conversations = ac;
        Log.d("oma","Conv size:"+conversations.size());
        cids = new ArrayList<>();
        for (Conversation c : conversations){
            cids.add(Integer.toString(c.getID()));
        }
        Log.d("oma","CIDS size:"+cids.size());
    }

    private void setCids(ArrayList<String> as){
        cids = as;
    }

    @Override
    public void update() {
        cont = true;
    }
}
