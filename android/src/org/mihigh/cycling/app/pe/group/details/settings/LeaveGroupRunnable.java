package org.mihigh.cycling.app.pe.group.details.settings;

import android.support.v4.app.FragmentActivity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.Utils;
import org.mihigh.cycling.app.filter.ExceptionHandler;
import org.mihigh.cycling.app.http.HttpHelper;
import org.mihigh.cycling.app.pe.PEHome;
import org.mihigh.cycling.app.pe.group.dto.PEGroupDetails;
import org.mihigh.cycling.app.utils.LoadingUtils;
import org.mihigh.cycling.app.utils.Navigation;

import java.io.IOException;

public class LeaveGroupRunnable implements Runnable {
    private static final String PATH_CREATE_GROUP = "/api/v1/group/%s";

    private FragmentActivity activity;
    private PEGroupDetails groupDetails;


    public LeaveGroupRunnable(FragmentActivity activity, PEGroupDetails groupDetails) {
        this.activity = activity;
        this.groupDetails = groupDetails;
    }

    @Override
    public void run() {
        String url = activity.getString(R.string.server_url) + String.format(PATH_CREATE_GROUP, groupDetails.id);

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpDelete httpCall = new HttpDelete(url);

            // Auth headers
            httpCall.addHeader("Cookie", Utils.SESSION_ID + " = " + HttpHelper.session);
            httpCall.setHeader(HTTP.CONTENT_TYPE, "application/json");

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httpCall);

            // Check if 202
            if (response.getStatusLine().getStatusCode() > 300) {
                throw new IOException("Received " + response.getStatusLine().getStatusCode());
            }

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    PEGroupDetails.setHasGroup(activity, false);
                    Navigation.changeFragment(activity, R.id.login_fragment_container, new PEHome());
                }
            });
        } catch (Exception e) {
            new ExceptionHandler(activity).sendError(e, false);
            LoadingUtils.makeToast(activity, "Try again..");
        }
    }
}
