package com.metropolia.kim.loboandroid;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.metropolia.kim.loboandroiddata.Alert;
import com.metropolia.kim.loboandroiddata.Conversation;
import com.metropolia.kim.loboandroiddata.Message;
import com.metropolia.kim.loboandroiddata.Worker;
import com.metropolia.kim.xmlparser.AlertXmlParser;
import com.metropolia.kim.xmlparser.ConversationXmlParser;
import com.metropolia.kim.xmlparser.MessageXmlParser;
import com.metropolia.kim.xmlparser.WorkerXmlParser;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


public class NetworkingTask extends AsyncTask<String, String, String> {
    private HttpURLConnection httpURLConnection;
    //private String baseurl = "http://192.168.43.9:8080/LoboChat/";// kim
    //private String baseurl = "http://192.168.43.109:8080/LoboChat/"; //Henks
    private String baseurl = "http://192.168.0.14:8080/LoboChat/"; //Henks hima
    private Context context;
    private Obsrvr observer;

    public NetworkingTask(Context context) {
        this.context = context;
    }

    public void register(Obsrvr o){
        observer = o;
    }

    @Override
    protected String doInBackground(String... params) {
        String endurl = params[0];
        String dataType = params[1];
        try {
            URL url = new URL(baseurl + endurl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(20000);
            httpURLConnection.setReadTimeout(20000);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("GET");
            //httpURLConnection.setRequestProperty("Content-Type","application/xml");

            httpURLConnection.connect();
            InputStream is = httpURLConnection.getInputStream();

            switch (dataType) {
                case "conversation":
                    ConversationXmlParser xmlParser = new ConversationXmlParser();
                    List<Conversation> conversations = xmlParser.parse(is);
                    Uri deleteUri = Uri.parse(ChatProvider.URL+"/members/flush");
                    this.context.getContentResolver().delete(deleteUri,null,null);
                    /*Uri deleteMes = Uri.parse(ChatProvider.URL+"/messages/flush");
                    this.context.getContentResolver().delete(deleteMes,null,null);*/
                    Log.d("oma","Koko: "+conversations.size());
                    for (Conversation c : conversations){
                        ContentValues values = new ContentValues();
                        values.put("_id", c.getID());
                        values.put("topic",c.getTopic());
                        Uri uri = Uri.parse(ChatProvider.URL + "/conversations/insert");
                        this.context.getContentResolver().insert(uri, values);
                        List<Message> messages = c.getMessages();
                        String lastMessage = "";
                        for (Message m : messages){
                            ContentValues val = new ContentValues();
                            lastMessage = m.getPostName()+": "+m.getContent();
                            val.put("content",m.getContent());
                            val.put("conversationid",m.getConversationID());
                            val.put("postname",m.getPostName());
                            val.put("shorttimestamp",m.getShortTime());
                            val.put("messageid",m.getMessageID());
                            Uri uri2 = Uri.parse(ChatProvider.URL + "/messages/insert");
                            this.context.getContentResolver().insert(uri2, val);
                        }

                        List<Worker> members = c.getMemberList();
                        for (Worker w : members){
                            ContentValues memVal = new ContentValues();
                            memVal.put("conversationid",c.getID());
                            memVal.put("topic",c.getTopic());
                            memVal.put("lastmessage",lastMessage);
                            memVal.put("workername",w.getName());
                            Uri memU = Uri.parse(ChatProvider.URL + "/members/insert");
                            this.context.getContentResolver().insert(memU, memVal);
                        }
                    }
                    break;

                case "message":
                    MessageXmlParser messsageParser = new MessageXmlParser();
                    List<Message> messages = messsageParser.parse(is);
                    for(Message m : messages){
                        ContentValues values = new ContentValues();
                        values.put("content",m.getContent());
                        values.put("conversationid",m.getConversationID());
                        values.put("postname",m.getPostName());
                        values.put("shorttimestamp",m.getShortTime());
                        values.put("messageid",m.getMessageID());
                        Uri uri = Uri.parse(ChatProvider.URL+"/messages/insert");
                        this.context.getContentResolver().insert(uri, values);
                    }
                    break;

                case "worker":
                    WorkerXmlParser workerParser = new WorkerXmlParser();
                    List<Worker> workers = workerParser.parse(is);
                    for(Worker w : workers){
                        ContentValues values = new ContentValues();

                        values.put("name", w.getName());
                        values.put("professionid", w.getGroupID());
                        if(w.getTitle().equals("Psychotherapist")){
                            values.put("title", "Therapist");
                        } else {
                            values.put("title", w.getTitle());
                        }
                        values.put("workerid", w.getId());
                        Uri uri = Uri.parse(ChatProvider.URL + "/workers/insert");
                        this.context.getContentResolver().insert(uri, values);

                    }
                    break;

                case "alert":
                    //Empty alert table
                    Uri emptyAlerts = Uri.parse(ChatProvider.URL + "/alerts/flush");
                    this.context.getContentResolver().delete(emptyAlerts, null, null);
                    //Parse alert history
                    AlertXmlParser alertParser = new AlertXmlParser(); //Parse history list
                    List<Alert> alerts = alertParser.parse(is);
                    for (Alert a : alerts) {
                        ContentValues values = new ContentValues();

                        values.put("topic", a.getAlertTopic());
                        values.put("currenttime", a.getCurrentTime());
                        values.put("category", a.getAlertCat());
                        values.put("postname", a.getPostName());
                        values.put("posttitle", a.getPostTitle());
                        values.put("receivergroup", a.getReceiverGroup());

                        Uri uri = Uri.parse(ChatProvider.URL + "/alerts/insert");
                        this.context.getContentResolver().insert(uri, values);
                    }
                    break;
            }
            is.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        observer.update();
        observer = null;

    }
}
