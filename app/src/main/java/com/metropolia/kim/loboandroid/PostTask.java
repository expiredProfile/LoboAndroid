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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by Tommi on 3.5.2016.
 */
public class PostTask extends AsyncTask<String, String, String> {
    private HttpURLConnection httpURLConnection;

    private String baseurl = "http://192.168.43.9:8080/LoboChat/";// kim
    //private String baseurl = "http://192.168.43.109:8080/LoboChat/"; //Henks
    //private String baseurl = "http://192.168.43.9:8080/LoboChat/";// kim
    //private String baseurl = "http://192.168.43.109:8080/LoboChat/"; //Henks
    //private String baseurl = "http://192.168.0.14:8080/LoboChat/"; //Henks hima
    //private String baseurl = "http://10.0.2.2:8080/LoboChat/"; //tommi
    private Obsrvr obsrvr;


    private Context context;
    public PostTask(Context context) {
        this.context = context;
    }


    public void register(Obsrvr o){
        obsrvr = o;
    }
    @Override
    protected String doInBackground(String... params) {
        String endurl = params[0];
        String dataType = params[1];
        String xml = params[2];

        try {
            URL url = new URL(baseurl + endurl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(20000);
            httpURLConnection.setReadTimeout(20000);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type","application/xml");

            httpURLConnection.connect();

            OutputStream os = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            //InputStream is = httpURLConnection.getInputStream();

            switch (dataType) {
                case "conversation":
                    bufferedWriter.write(xml);
                    bufferedWriter.flush();
                    bufferedWriter.close();

                    int responseCode = httpURLConnection.getResponseCode();
                    Log.d("kek", "response: " + responseCode);
                    os.close();
                    break;

                case "message":
                    bufferedWriter.write(xml);
                    bufferedWriter.flush();
                    bufferedWriter.close();

                    int resp = httpURLConnection.getResponseCode();
                    Log.d("kek", "response: " + resp);
                    os.close();
                    break;
                case "alert":
                    bufferedWriter.write(xml);
                    bufferedWriter.flush();
                    bufferedWriter.close();

                    int alertResponseCode = httpURLConnection.getResponseCode();
                    Log.d("POST", "alert post response: " + alertResponseCode);
                    /*
                    try {
                        InputStream is = httpURLConnection.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                        int newAlertId = Integer.parseInt(bufferedReader.readLine());
                        Log.d("ALERT POST", "Server returned alert id: " + newAlertId);
                        is.close();
                        //get alert by ID for notification etc
                    } catch (Exception e) {
                        Log.d("ALERT POST", "Exception in inputStream: " + e);
                    }
                    */
                    os.close();
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
