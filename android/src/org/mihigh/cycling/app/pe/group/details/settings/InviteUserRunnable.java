package org.mihigh.cycling.app.pe.group.details.settings;

import android.app.ProgressDialog;
import android.support.v4.app.FragmentActivity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.Utils;
import org.mihigh.cycling.app.filter.ExceptionHandler;
import org.mihigh.cycling.app.http.HttpHelper;
import org.mihigh.cycling.app.pe.group.dto.PEGroupDetails;
import org.mihigh.cycling.app.utils.LoadingUtils;

import java.io.IOException;

public class InviteUserRunnable implements Runnable {
    private static final String PATH_CREATE_GROUP = "/api/v1/group/%s/memeber/%s";

    private FragmentActivity activity;
    private PEGroupDetails groupDetails;
    private final String email;
    private final ProgressDialog loadingDialog;


    public InviteUserRunnable(FragmentActivity activity, PEGroupDetails groupDetails, String email, ProgressDialog loadingDialog) {
        this.activity = activity;
        this.groupDetails = groupDetails;
        this.email = email;
        this.loadingDialog = loadingDialog;
    }


    @Override
    public void run() {
        String url = activity.getString(R.string.server_url) + String.format(PATH_CREATE_GROUP, groupDetails.id, email);

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpCall = new HttpPost(url);

            // Auth headers
            httpCall.addHeader("Cookie", Utils.SESSION_ID + " = " + HttpHelper.session);
            httpCall.setHeader(HTTP.CONTENT_TYPE, "application/json");

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httpCall);

            // Check if 202
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_ACCEPTED) {
                throw new IOException("Received " + response.getStatusLine().getStatusCode());
            }

        } catch (Exception e) {
            new ExceptionHandler(activity).sendError(e, false);
            LoadingUtils.makeToast(activity, "Try again..");
        }

        loadingDialog.dismiss();
    }
}
