package org.mihigh.cycling.app.pe.group.join.search;

import android.app.Dialog;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.Utils;
import org.mihigh.cycling.app.filter.ExceptionHandler;
import org.mihigh.cycling.app.http.HttpHelper;
import org.mihigh.cycling.app.pe.group.dto.PEGroupDetails;

import java.io.IOException;

public class RequestInvitationRunnable implements Runnable {
    private static final String PATH_CREATE_GROUP = "/api/v1/invitation/%s";

    private final PEGroupDetails groupDetails;
    private final FragmentActivity activity;
    private final Dialog progress;


    public RequestInvitationRunnable(PEGroupDetails groupDetails, FragmentActivity activity, Dialog progress) {
        this.groupDetails = groupDetails;
        this.activity = activity;
        this.progress = progress;
    }

    @Override
    public void run() {
        String url = activity.getString(R.string.server_url) + String.format(PATH_CREATE_GROUP, groupDetails.id);

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpCall = new HttpPost(url);

            // Auth headers
            httpCall.addHeader("Cookie", Utils.SESSION_ID + " = " + HttpHelper.session);
            httpCall.setHeader(HTTP.CONTENT_TYPE, "application/json");

            // Add your data
            httpCall.setEntity(new StringEntity("create"));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httpCall);

            // Check if 202
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_ACCEPTED) {
                throw new IOException("Received " + response.getStatusLine().getStatusCode());
            }

            // all good
            progress.dismiss();
        } catch (Exception e) {
            new ExceptionHandler(activity).sendError(e, false);

            progress.dismiss();
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(activity, "Try again..", Toast.LENGTH_LONG).show();
                }
            });
        }



    }
}
