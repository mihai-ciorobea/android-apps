package org.mihigh.cycling.app.group;

import android.app.ProgressDialog;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.mihigh.cycling.app.LoginActivity;
import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.Utils;
import org.mihigh.cycling.app.http.HttpHelper;

public class SaveGroupRideRunnable implements Runnable {

    public static final String PATH_SAVE_GROUP_ACTIVITY = "/api/v1/tracking/finish";
    private final ProgressDialog progress;
    private final LoginActivity activity;

    public SaveGroupRideRunnable(ProgressDialog progress, LoginActivity activity) {
        this.progress = progress;
        this.activity = activity;
    }

    @Override
    public void run() {
        String url = activity.getString(R.string.server_url) + PATH_SAVE_GROUP_ACTIVITY;

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            httpGet.addHeader("Cookie", Utils.SESSION_ID + " = " + HttpHelper.session);
            httpGet.setHeader(HTTP.CONTENT_TYPE, "application/json");

            // Execute HTTP Post Request
            httpclient.execute(httpGet);
        } catch (Exception e) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
            }
            run();
        }

        progress.dismiss();
        activity.onUserLoggedIn();

    }
}