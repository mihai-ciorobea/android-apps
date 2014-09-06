package org.mihigh.cycling.app.solo;

import android.app.ProgressDialog;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.mihigh.cycling.app.LoginActivity;
import org.mihigh.cycling.app.R;
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

        HttpResponse httpResponse = null;
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            httppost.addHeader("Cookie","SESSION0 = " + HttpHelper.session);
            httppost.setHeader(HTTP.CONTENT_TYPE, "application/json");

            // Add your data
            httppost.setEntity(new StringEntity(jsonData));

            // Execute HTTP Post Request
            httpResponse = httpclient.execute(httppost);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (httpResponse.getStatusLine().getStatusCode() >= 300) {
            //TODO: error handling
        }

        progress.dismiss();
        activity.onUserLoggedIn();

    }
}