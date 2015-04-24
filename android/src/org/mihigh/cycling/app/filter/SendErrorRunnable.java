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
    private boolean kill;
    private int pid;

    private static final ArrayList<SendErrorRunnable> oldErrors = new ArrayList<SendErrorRunnable>();

    public SendErrorRunnable(Activity activity, String errorData, boolean kill, int pid) {
        this.activity = activity;
        this.errorData = errorData;
        this.kill = kill;
        this.pid = pid;
    }

    @Override
    public void run() {
        execute();

        ArrayList<SendErrorRunnable> retryErrors = new ArrayList<SendErrorRunnable>(oldErrors);
        oldErrors.clear();

        for (SendErrorRunnable error : retryErrors) {
            if (error != null) {
                if (!error.execute()) {
                    oldErrors.add(error);
                }
            }
        }

        if (kill) {
            android.os.Process.killProcess(pid);
        }

        try {
            Thread.sleep(1000 * 60);
        } catch (InterruptedException ignored) {
        }
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
            return false;
        }
    }
}