package org.mihigh.cycling.app.solo;

import android.app.ProgressDialog;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.mihigh.cycling.app.LoginActivity;

public class SaveRideRunnable implements Runnable {

    //TODO URL
    public static final String URL = "http://www.yoursite.com/script.php";
    private final String jsonData;
    private final ProgressDialog progress;
    private final LoginActivity activity;

    public SaveRideRunnable(String jsonData, ProgressDialog progress, LoginActivity activity) {

        this.jsonData = jsonData;
        this.progress = progress;
        this.activity = activity;
    }

    @Override
    public void run() {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(URL);

            // Add your data
            httppost.setEntity(new StringEntity(jsonData));

            // Execute HTTP Post Request
            httpclient.execute(httppost);
        } catch (Exception e) {
            e.printStackTrace();
        }
        progress.dismiss();
        activity.onUserLoggedIn();
    }
}