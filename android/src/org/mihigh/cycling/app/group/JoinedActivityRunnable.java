package org.mihigh.cycling.app.group;

import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.mihigh.cycling.app.LoginActivity;
import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.Utils;
import org.mihigh.cycling.app.http.HttpHelper;
import org.mihigh.cycling.app.login.dto.UserInfo;

public class JoinedActivityRunnable implements Runnable {

    private static final String PATH_GET_JOINED_LIST = "/api/v1/activities/join";
    private final SearchRideDetailsFragment fragment;
    private long id;

    public JoinedActivityRunnable(SearchRideDetailsFragment fragment, long id) {
        this.fragment = fragment;
        this.id = id;
    }

    @Override
    public void run() {
        String url = fragment.getString(R.string.server_url) + PATH_GET_JOINED_LIST;

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            httppost.addHeader("Cookie", Utils.SESSION_ID + " = " + HttpHelper.session);
            httppost.setHeader(HTTP.CONTENT_TYPE, "application/json");
            httppost.setHeader(Utils.EMAIL, UserInfo.restore(fragment.getActivity()) == null ? null : UserInfo.restore(fragment.getActivity()).getEmail());

            httppost.setEntity(new StringEntity(id + ""));

            // Execute HTTP Post Request
            httpclient.execute(httppost);
        } catch (Exception e) {
            Toast.makeText(fragment.getActivity(), "Check internet connection!", Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        }

        fragment.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((LoginActivity) (fragment.getActivity())).searchForRide();
            }
        });
    }
}



