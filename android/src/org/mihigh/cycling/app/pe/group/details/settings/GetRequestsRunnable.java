package org.mihigh.cycling.app.pe.group.details.settings;

import android.app.ProgressDialog;
import android.support.v4.app.FragmentActivity;
import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.Utils;
import org.mihigh.cycling.app.filter.ExceptionHandler;
import org.mihigh.cycling.app.http.HttpHelper;
import org.mihigh.cycling.app.login.dto.UserInfo;
import org.mihigh.cycling.app.pe.group.dto.PEGroupDetails;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class GetRequestsRunnable implements Runnable {

    private static final String PATH_CREATE_GROUP = "/api/v1/request/group/%s";
    private final FragmentActivity activity;
    private final PEGroupDetails groupDetails;
    private final PEJoinRequestListAdapter adapter;
    private final ProgressDialog loadingDialog;

    public GetRequestsRunnable(FragmentActivity activity, PEGroupDetails groupDetails, PEJoinRequestListAdapter adapter, ProgressDialog loadingDialog) {
        this.activity = activity;
        this.groupDetails = groupDetails;
        this.adapter = adapter;
        this.loadingDialog = loadingDialog;
    }


    @Override
    public void run() {
        String url = activity.getString(R.string.server_url) + String.format(PATH_CREATE_GROUP, groupDetails.id);

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpCall = new HttpGet(url);

            // Auth headers
            httpCall.addHeader("Cookie", Utils.SESSION_ID + " = " + HttpHelper.session);
            httpCall.setHeader(HTTP.CONTENT_TYPE, "application/json");
            httpCall.setHeader(Utils.EMAIL, UserInfo.restore(activity) == null ? null : UserInfo.restore(activity).getEmail());

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httpCall);

            // Check if 200
            if (response.getStatusLine().getStatusCode() > 300) {
                throw new IOException("Received " + response.getStatusLine().getStatusCode());
            }

            Type type = new TypeToken<List<UserInfo>>() {
            }.getType();
            final List<UserInfo> users = HttpHelper.fromInputStream(response.getEntity().getContent(), type);

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (users == null || users.isEmpty()) {
                        return;
                    }

                    adapter.clear();
                    for (UserInfo user : users) {
                        adapter.add(new RequestData(groupDetails, user));
                    }
                    adapter.notifyDataSetChanged();
                }
            });
        } catch (Exception e) {
            new ExceptionHandler(activity).sendError(e, false);
        }

        loadingDialog.dismiss();
    }


}
