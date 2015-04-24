package org.mihigh.cycling.app.pe.groups.create;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
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
import org.mihigh.cycling.app.pe.PEGroupDetails;
import org.mihigh.cycling.app.utils.Mapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CreateGroupRunnable implements Runnable {
    private static final String PATH_CREATE_GROUP = "/api/v1/group";
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

            // Add your data
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("name", groupName);
            data.put("users", users.toString());
            httpCall.setEntity(new StringEntity(HttpHelper.getGson().toJson(data)));

            // Execute HTTP Post Request
            HttpResponse execute = httpclient.execute(httpCall);

            // Check if 202
            if (execute.getStatusLine().getStatusCode() != HttpStatus.SC_ACCEPTED) {
                throw new IOException("Received " + execute.getStatusLine().getStatusCode());
            } else {
                // all good
                Long groupId = Mapping.<Long>map(execute.getEntity().getContent(), Long.TYPE);
                if (groupId == null) {
                    String errorDetails = "Create group return null value";
                    new ExceptionHandler(activity).sendError(new RuntimeException(errorDetails), false);
                }

                new PEGroupDetails(groupId, groupName).store(activity);
            }
        } catch (Exception e) {
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
