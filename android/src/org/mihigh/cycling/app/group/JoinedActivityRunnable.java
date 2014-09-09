package org.mihigh.cycling.app.group;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.mihigh.cycling.app.LoginActivity;
import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.group.dto.JoinedRide;
import org.mihigh.cycling.app.http.HttpHelper;

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

        HttpResponse httpResponse = null;
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            httppost.addHeader("Cookie", "SESSION0 = " + HttpHelper.session);
            httppost.setHeader(HTTP.CONTENT_TYPE, "application/json");

            httppost.setEntity(new StringEntity(id + ""));

            // Execute HTTP Post Request
            httpResponse = httpclient.execute(httppost);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (httpResponse.getStatusLine().getStatusCode() >= 300) {
            //TODO: error handling
        }

        fragment.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((LoginActivity) (fragment.getActivity())).searchForRide();
            }
        });

    }
}



