package com.metropolia.kim.loboandroid;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.metropolia.kim.loboandroiddata.Conversation;
import com.metropolia.kim.loboandroiddata.Worker;
import com.metropolia.kim.xmlparser.ConversationXmlParser;
import com.metropolia.kim.xmlparser.WorkerXmlParser;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


public class NetworkingTask extends AsyncTask<String, String, String> {
    private HttpURLConnection httpURLConnection;
    private String baseurl = "http://10.0.2.2:8080/LoboChat/";
    private Context context;

    public NetworkingTask(Context context) {
        this.context = context;
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
            httpURLConnection.setRequestProperty("Content-Type","application/xml");

            httpURLConnection.connect();
            InputStream is = httpURLConnection.getInputStream();

            switch (dataType) {
                case "conversation":
                    ConversationXmlParser xmlParser = new ConversationXmlParser();
                    List<Conversation> conversations = xmlParser.parse(is);

                case "message":

                case "worker":
                    WorkerXmlParser workerParser = new WorkerXmlParser();
                    List<Worker> workers = workerParser.parse(is);
                    for(Worker w : workers){
                        ContentValues values = new ContentValues();

                        values.put("name", w.getName());
                        values.put("professionid", w.getGroupID());
                        values.put("title", w.getTitle());
                        Log.d("TEST", w.getName());
                        Log.d("TEST", w.getTitle());
                        Log.d("TEST",".." +  w.getGroupID());

                        this.context.getContentResolver().insert(ChatProvider.CONTENT_URI, values);

                    }
                case "alert":
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}