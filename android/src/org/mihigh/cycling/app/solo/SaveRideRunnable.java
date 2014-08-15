package org.mihigh.cycling.app.solo;

import android.app.ProgressDialog;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.mihigh.cycling.app.LoginActivity;
import org.mihigh.cycling.app.R;

public class SaveRideRunnable implements Runnable {

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
        String url = activity.getString(R.string.server_url);


        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);

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