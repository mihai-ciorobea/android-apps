package org.mihigh.cycling.app.pe.group.create;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CreateGroupRunnable implements Runnable {
    private static final String PATH_CREATE_GROUP = "/api/v1/wrapper/group";
    private final String groupName;
    private final ArrayList<String> users;
    private Fragment fragment;
    private ProgressDialog progress;
    private FragmentActivity activity;

    public CreateGroupRunnable(String groupName, ArrayList<String> users, Fragment fragment, ProgressDialog progress) {
        this.groupName = groupName;
        this.users = users;
        this.fragment = fragment;
        this.progress = progress;

        activity = fragment.getActivity();
    }

    @Override
    public void run() {
        String url = fragment.getString(R.string.server_url) + PATH_CREATE_GROUP;

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpCall = new HttpPost(url);

            // Auth headers
            httpCall.addHeader("Cookie", Utils.SESSION_ID + " = " + HttpHelper.session);
            httpCall.setHeader(HTTP.CONTENT_TYPE, "application/json");
            httpCall.setHeader(Utils.EMAIL, UserInfo.restore(fragment.getActivity()) == null ? null : UserInfo.restore(fragment.getActivity()).getEmail());


            // Add your data
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("name", groupName);
            data.put("users", users);
            httpCall.setEntity(new StringEntity(HttpHelper.getGson().toJson(data)));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httpCall);

            // Check if 202
            if (response.getStatusLine().getStatusCode() > 300) {
                throw new IOException("Received " + response.getStatusLine().getStatusCode());
            }

            // all good
            new PEGroupDetails().setHasGroup(activity, true);
        } catch (Exception e) {
            new ExceptionHandler(activity).sendError(e, false);

            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(activity, "Try again..", Toast.LENGTH_LONG).show();
                    progress.dismiss();
                }
            });
            return;
        }

        progress.dismiss();
        fragment.getFragmentManager().popBackStack();

    }
}
