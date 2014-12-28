package org.mihigh.cycling.app.solo;

import android.app.ProgressDialog;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.mihigh.cycling.app.LoginActivity;
import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.Utils;
import org.mihigh.cycling.app.http.HttpHelper;

public class SaveSoloRideRunnable implements Runnable {

    public static final String PATH_SAVE_SOLO_ACTIVITY = "/api/v1/activities/solo/activity/";
    private final String jsonData;
    private String distance;
    private final ProgressDialog progress;
    private final LoginActivity activity;

    public SaveSoloRideRunnable(String jsonData, String distance, ProgressDialog progress, LoginActivity activity) {
        this.jsonData = jsonData;
        this.distance = distance;
        this.progress = progress;
        this.activity = activity;
    }

    @Override
    public void run() {
        String url = activity.getString(R.string.server_url) + PATH_SAVE_SOLO_ACTIVITY + distance;

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            httppost.addHeader("Cookie", Utils.SESSION_ID + " = " + HttpHelper.session);
            httppost.setHeader(HTTP.CONTENT_TYPE, "application/json");

            // Add your data
            httppost.setEntity(new StringEntity(jsonData));

            // Execute HTTP Post Request
            httpclient.execute(httppost);
        } catch (Exception e) {
            Toast.makeText(activity, "Check internet connection!", Toast.LENGTH_SHORT).show();
            progress.dismiss();
        }


        progress.dismiss();
        activity.onUserLoggedIn();

    }
}