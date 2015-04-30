package org.mihigh.cycling.app.pe.route.help;

import android.app.ProgressDialog;
import android.support.v4.app.FragmentActivity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.Utils;
import org.mihigh.cycling.app.filter.ExceptionHandler;
import org.mihigh.cycling.app.http.HttpHelper;
import org.mihigh.cycling.app.utils.LoadingUtils;

import java.io.IOException;
import java.util.HashMap;

public class NeedHelpRunnable implements Runnable {

    private static final String PATH_CREATE_GROUP = "/api/v1/activity/PE/help";

    private final FragmentActivity activity;
    private final String text;
    private final boolean group;
    private final boolean nearby;
    private final ProgressDialog loadingDialog;

    public NeedHelpRunnable(FragmentActivity activity, String text, boolean group, boolean nearby, ProgressDialog loadingDialog) {

        this.activity = activity;
        this.text = text;
        this.group = group;
        this.nearby = nearby;
        this.loadingDialog = loadingDialog;
    }

    @Override
    public void run() {
        String url = activity.getString(R.string.server_url) + PATH_CREATE_GROUP;

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpCall = new HttpPost(url);

            // Auth headers
            httpCall.addHeader("Cookie", Utils.SESSION_ID + " = " + HttpHelper.session);
            httpCall.setHeader(HTTP.CONTENT_TYPE, "application/json");

            // Add text line
            // Add your data
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("text", text);
            data.put("group", group);
            data.put("nearby", nearby);
            httpCall.setEntity(new StringEntity(HttpHelper.getGson().toJson(data)));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httpCall);

            // Check if 202
            if (response.getStatusLine().getStatusCode() > 300) {
                throw new IOException("Received " + response.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            new ExceptionHandler(activity).sendError(e, false);

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LoadingUtils.makeToast(activity, "Try again");
                }
            });
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingDialog.dismiss();
                LoadingUtils.makeToast(activity, "Message sent");
                activity.getFragmentManager().popBackStack();
            }
        });

    }
}
