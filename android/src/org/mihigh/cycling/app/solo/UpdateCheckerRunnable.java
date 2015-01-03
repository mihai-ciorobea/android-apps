package org.mihigh.cycling.app.solo;

import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.mihigh.cycling.app.LoginActivity;
import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.filter.ExceptionHandler;
import org.mihigh.cycling.app.utils.Mapping;

public class UpdateCheckerRunnable implements Runnable {

    private static final String PATH_CHECK_UPDATES = "/api/v1/updates";
    private LoginActivity activity;

    public UpdateCheckerRunnable(LoginActivity loginActivity) {
        activity = loginActivity;
    }

    @Override
    public void run() {
        try {
            int versionCode = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionCode;

            String url = activity.getString(R.string.server_url) + PATH_CHECK_UPDATES;

            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpCall = new HttpGet(url);
            httpCall.setHeader(HTTP.CONTENT_TYPE, "application/json");

            // Execute HTTP Post Request
            HttpResponse execute = httpclient.execute(httpCall);

            Integer lastVersion = Mapping.<Integer>map(execute.getEntity().getContent(), Integer.TYPE);
            if (lastVersion > versionCode) {
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        activity.showUpdates();
                    }
                });
            }
        } catch (Exception e) {
            new ExceptionHandler(activity).sendError(e, false);
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(activity, "Check internet connection!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}