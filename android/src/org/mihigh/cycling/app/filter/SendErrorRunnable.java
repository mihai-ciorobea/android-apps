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
import org.mihigh.cycling.app.login.dto.UserInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class SendErrorRunnable implements Runnable {

    public static final String PATH = "/api/v1/error";
    private Activity activity;
    private String errorData;
    private boolean kill;
    private int pid;


    final static List<SendErrorRunnable> failedUpdates = Collections.synchronizedList(new ArrayList<SendErrorRunnable>());


    public SendErrorRunnable(Activity activity, String errorData, boolean kill, int pid) {
        this.activity = activity;
        this.errorData = errorData;
        this.kill = kill;
        this.pid = pid;
    }

    @Override
    public void run() {
        boolean succeeded = sendError();

        synchronized (failedUpdates) {
            if (succeeded) {
                //try others
                for (Iterator<SendErrorRunnable> iterator = failedUpdates.iterator(); iterator.hasNext(); ) {
                    SendErrorRunnable failedUpdate = iterator.next();
                    failedUpdate.activity = activity;
                    boolean retryStatus = failedUpdate.sendError();
                    if (!retryStatus) {
                        break;
                    } else {
                        iterator.remove();
                    }
                }
            } else {
                //add to failed
                failedUpdates.add(this);
            }
        }


        if (kill) {
            android.os.Process.killProcess(pid);
        }

        try {
            Thread.sleep(1000 * 10);
        } catch (InterruptedException ignored) {
        }
    }


    private boolean sendError() {
        String url = activity.getString(R.string.server_url) + PATH;

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            httppost.addHeader("Cookie", Utils.SESSION_ID + " = " + HttpHelper.session);
            httppost.setHeader(HTTP.CONTENT_TYPE, "application/json");
            httppost.setHeader(Utils.EMAIL, UserInfo.restore(activity) == null ? null : UserInfo.restore(activity).getEmail());

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