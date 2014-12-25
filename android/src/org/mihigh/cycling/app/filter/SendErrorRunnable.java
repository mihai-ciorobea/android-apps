package org.mihigh.cycling.app.filter;

import android.app.Activity;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.Utils;
import org.mihigh.cycling.app.http.HttpHelper;

public class SendErrorRunnable implements Runnable {

    public static final String PATH = "/api/v1/error";
  private final Activity activity;
    private String errorData;
    private int pid;

    public SendErrorRunnable(Activity activity, String errorData, int pid) {
        this.activity = activity;
        this.errorData = errorData;
        this.pid = pid;
    }

    @Override
    public void run() {
        String url = activity.getString(R.string.server_url) + PATH;

        HttpResponse httpResponse = null;
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            httppost.addHeader("Cookie", Utils.SESSION_ID + " = " + HttpHelper.session);
            httppost.setHeader(HTTP.CONTENT_TYPE, "application/json");

            // Add your data
            httppost.setEntity(new StringEntity(errorData));

            // Execute HTTP Post Request
            httpResponse = httpclient.execute(httppost);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (httpResponse.getStatusLine().getStatusCode() >= 300) {

        }

        android.os.Process.killProcess(pid);
    }
}