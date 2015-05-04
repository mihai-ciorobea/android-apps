package org.mihigh.cycling.app.pe.group.details.settings;

import android.app.ProgressDialog;
import android.support.v4.app.FragmentActivity;
import android.widget.ArrayAdapter;
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
import org.mihigh.cycling.app.login.dto.UserInfo;
import org.mihigh.cycling.app.pe.group.dto.PEGroupDetails;
import org.mihigh.cycling.app.utils.LoadingUtils;

import java.io.IOException;

public class InviteUserRunnable implements Runnable {
    private static final String PATH_CREATE_GROUP = "/api/v1/request/group/%s/invitation";

    private FragmentActivity activity;
    private PEGroupDetails groupDetails;
    private final String email;
    private final ProgressDialog loadingDialog;
    private final ArrayAdapter<String> invitesAdapter;
    private final String id;


    public InviteUserRunnable(FragmentActivity activity, PEGroupDetails groupDetails, String email, ProgressDialog loadingDialog,
                              ArrayAdapter<String> invitesAdapter, String id) {
        this.activity = activity;
        this.groupDetails = groupDetails;
        this.email = email;
        this.loadingDialog = loadingDialog;
        this.invitesAdapter = invitesAdapter;
        this.id = id;
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
            httpCall.setHeader(Utils.EMAIL, UserInfo.restore(activity) == null ? null : UserInfo.restore(activity).getEmail());


            httpCall.setEntity(new StringEntity(email));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httpCall);

            // Check if 202
            if (response.getStatusLine().getStatusCode() > 300) {
                throw new IOException("Received " + response.getStatusLine().getStatusCode());
            }

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    invitesAdapter.insert(id, 0);
                }
            });

        } catch (Exception e) {
            new ExceptionHandler(activity).sendError(e, false);
            LoadingUtils.makeToast(activity, "Try again..");
        }

        loadingDialog.dismiss();
    }
}
