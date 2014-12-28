package org.mihigh.cycling.app.group;

import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.Utils;
import org.mihigh.cycling.app.group.dto.JoinedRide;
import org.mihigh.cycling.app.http.HttpHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GroupGetJoinedListRunnable implements Runnable {

    private static final String PATH_GET_JOINED_LIST = "/api/v1/activities/joined";
    private final GroupJoinedListFragment fragment;

    public GroupGetJoinedListRunnable(GroupJoinedListFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void run() {
        String url = fragment.getString(R.string.server_url) + PATH_GET_JOINED_LIST;

        HttpResponse httpResponse = null;
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httppost = new HttpGet(url);
            httppost.addHeader("Cookie", Utils.SESSION_ID  + " = " + HttpHelper.session);
            httppost.setHeader(HTTP.CONTENT_TYPE, "application/json");

            // Execute HTTP Post Request
            httpResponse = httpclient.execute(httppost);
        } catch (Exception e) {
            Toast.makeText(fragment.getActivity(), "Check internet connection!", Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        }

        BufferedReader reader;
        List<JoinedRide> yourClassList;
        try {
            reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
            String responseBody = reader.readLine();

            Type listType = new TypeToken<ArrayList<JoinedRide>>() {
            }.getType();

            yourClassList = HttpHelper.getGson().fromJson(responseBody, listType);
        } catch (IOException e) {
            Toast.makeText(fragment.getActivity(), "Check internet connection!", Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        }

        final List<JoinedRide> finalYourClassList = yourClassList;
        fragment.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fragment.populateList(finalYourClassList);
            }
        });

    }
}



