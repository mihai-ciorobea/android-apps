package org.mihigh.cycling.app.filter;

import android.app.Activity;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.Utils;
import org.mihigh.cycling.app.http.HttpHelper;

import java.util.ArrayList;

public class SendErrorRunnable implements Runnable {

    public static final String PATH = "/api/v1/error";
    private final Activity activity;
    private String errorData;
    private int pid;

    private static final ArrayList<SendErrorRunnable> old = new ArrayList<SendErrorRunnable>();

    public SendErrorRunnable(Activity activity, String errorData, int pid) {
        this.activity = activity;
        this.errorData = errorData;
        this.pid = pid;
    }

    @Override
    public void run() {
        execute();

        ArrayList<SendErrorRunnable> retry = new ArrayList<SendErrorRunnable>(old);
        old.clear();

        boolean sendHttpCall = true;
        for (SendErrorRunnable oldItem : retry) {
            if (oldItem != null) {
                if ( sendHttpCall == true) {
                    sendHttpCall = oldItem.execute();
                } else {
                    old.add(oldItem);
                }
            }
        }

        android.os.Process.killProcess(pid);
    }

    private boolean execute() {
        String url = activity.getString(R.string.server_url) + PATH;

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            httppost.addHeader("Cookie", Utils.SESSION_ID + " = " + HttpHelper.session);
            httppost.setHeader(HTTP.CONTENT_TYPE, "application/json");

            // Add your data
            httppost.setEntity(new StringEntity(errorData));

            // Execute HTTP Post Request
            httpclient.execute(httppost);
            return true;
        } catch (Exception e) {
            old.add(this);
            return false;
        }
    }
}