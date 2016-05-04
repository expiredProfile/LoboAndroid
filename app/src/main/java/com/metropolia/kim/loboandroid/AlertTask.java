package com.metropolia.kim.loboandroid;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.metropolia.kim.loboandroiddata.Alert;
import com.metropolia.kim.xmlparser.AlertXmlParser;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tommi on 4.5.2016.
 */
public class AlertTask extends AsyncTask<String, String, String> {
    private HttpURLConnection httpURLConnection;

    //private String baseurl = "http://192.168.43.9:8080/LoboChat/";// kim
    private String baseurl = "http://192.168.43.109:8080/LoboChat/"; //Henks
    //private String baseurl = "http://10.0.2.2:8080/LoboChat/"; //tommi
    private int professionId;

    private Context context;

    public AlertTask(Context context, String title) {
        this.context = context;
        switch (title){
            case "Doctor":
               professionId = 2;
                break;
            case "Guard":
                professionId = 1;
                break;
            default:
                professionId = 0;
                break;
        }
    }

    @Override
    protected String doInBackground(String... params) {
       // while (true) {
            int largestInDatabase = this.getAlertsFromDatabase();
            try {
                URL url = new URL(baseurl + "resources/Alerts/Alerthistory/2");
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(20000);
                httpURLConnection.setReadTimeout(20000);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setRequestMethod("GET");
                //httpURLConnection.setRequestProperty("Content-Type","application/xml");

                httpURLConnection.connect();
                InputStream is = httpURLConnection.getInputStream();


                AlertXmlParser alertParser = new AlertXmlParser();
                List<Alert> alerts = alertParser.parse(is);
                Log.d("kek", "" + alerts.size());
                for (Alert a : alerts) {
                    if (a.getID() > largestInDatabase) {
                        Log.d("kek", "......");
                        ContentValues values = new ContentValues();
                        values.put("topic", a.getAlertTopic());
                        values.put("currenttime", a.getCurrentTime());
                        values.put("category", a.getAlertCat());
                        values.put("postname", a.getPostName());
                        values.put("receivergroup", a.getReceiverGroup());
                        Uri uri = Uri.parse(ChatProvider.URL + "/alerts/insert");
                        this.context.getContentResolver().insert(uri, values);

                        if(a.getReceiverGroup() == professionId || a.getReceiverGroup() == 0){
                            this.sentNotification(a.getAlertTopic(), a.getPostName());
                        }
                    }
                }
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //SystemClock.sleep(10000);
       // }
        return null;
    }

    private void sentNotification(String topic, String postName) {
        //Service notification
        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_chat_bubble)
                .setAutoCancel(true)
                .setContentTitle(topic)
                .setContentText(postName);
        int NOTIF_ID = 1; //Sth here !

        Intent notifIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notifIntent, 0);
        notifBuilder.setContentIntent(pendingIntent);

        NotificationManager notifMan = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifMan.notify(NOTIF_ID, notifBuilder.build());
    }

    private int getAlertsFromDatabase() {
        ArrayList<Alert> alerts = new ArrayList<>();
        String URL = ChatProvider.URL + "/alerts";
        Uri alertsUri = Uri.parse(URL);
        int max = -1;
        Cursor c = this.context.getContentResolver().query(alertsUri, null, null, null, "_id");
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    int idTemp = Integer.parseInt(c.getString(c.getColumnIndex("_id")));
                    if (max < idTemp) {
                        max = idTemp;
                    }
                } while (c.moveToNext());
                Log.d("kek", "." + max);
            }//if
        }
        return max;
    }

}
