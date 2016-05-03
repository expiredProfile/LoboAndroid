package com.metropolia.kim.loboandroid;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.metropolia.kim.loboandroiddata.Worker;
import com.metropolia.kim.xmlparser.WorkerXmlParser;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hege on 02/05/2016.
 */
public class LoginTask extends AsyncTask<String, String, Boolean> {
    private Context context;
    private String baseurl = "http://192.168.43.109:8080/LoboChat/";
    private String wname = "";
    private String wtitle = "";


    public LoginTask(Context c) {
        this.context = c;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (aBoolean) {
            Intent intent = new Intent(this.context, MainActivity.class);
            intent.putExtra("workerName",wname);
            intent.putExtra("workerTitle", wtitle);
            this.context.startActivity(intent);
        } else {

        }
    }

    @Override
    protected Boolean doInBackground(String... params) {
        boolean allow = false;
        String name = params[0];
        try {
            URL url = new URL(baseurl + "resources/Workers/LoggedOut");

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(20000);
            httpURLConnection.setReadTimeout(20000);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Content-Type", "application/xml");

            httpURLConnection.connect();
            InputStream is = httpURLConnection.getInputStream();

            WorkerXmlParser workerParser = new WorkerXmlParser();
            List<Worker> workers = workerParser.parse(is);
            ArrayList<String> workerStr = new ArrayList<>();

            for(Worker w : workers){
                if (w.getName().equals(name)){
                    this.wname = name;
                    this.wtitle = w.getTitle();
                    allow = true;
                }
            }


        } catch (Exception e) {

        }
        return allow;
    }
}
