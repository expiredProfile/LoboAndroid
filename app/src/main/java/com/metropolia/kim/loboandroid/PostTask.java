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

import java.io.BufferedWriter;
import java.io.InputStream;
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

    //private String baseurl = "http://192.168.43.9:8080/LoboChat/";// kim
    private String baseurl = "http://192.168.43.109:8080/LoboChat/"; //Henks
    //private String baseurl = "http://10.0.2.2:8080/LoboChat/"; //tommi

    private Context context;
    public PostTask(Context context) {
        this.context = context;
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

                case "message":


                case "alert":

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
