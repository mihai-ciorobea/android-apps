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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.group.dto.JoinedRide;
import org.mihigh.cycling.app.http.HttpHelper;

public class GroupGetSearchListRunnable implements Runnable {

    private static final String PATH_GET_JOINED_LIST = "/api/v1/activities/search";
    private final SearchListFragment fragment;
    private String text;

    public GroupGetSearchListRunnable(SearchListFragment fragment, String text) {
        this.fragment = fragment;
        this.text = text;
    }

    @Override
    public void run() {
        String url = fragment.getString(R.string.server_url)  + PATH_GET_JOINED_LIST + "?query=" + text;

        HttpResponse httpResponse = null;
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httppost = new HttpGet(url);
            httppost.addHeader("Cookie", "SESSION0 = " + HttpHelper.session);
            httppost.setHeader(HTTP.CONTENT_TYPE, "application/json");

            // Execute HTTP Post Request
            httpResponse = httpclient.execute(httppost);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (httpResponse.getStatusLine().getStatusCode() >= 300) {
            //TODO: error handling
        }

        BufferedReader reader = null;
        List<JoinedRide> yourClassList = null;
        try {
            reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
            String responseBody = reader.readLine();

            Type listType = new TypeToken<ArrayList<JoinedRide>>() {
            }.getType();

            yourClassList = HttpHelper.getGson().fromJson(responseBody, listType);
        } catch (IOException e) {
            e.printStackTrace();
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



