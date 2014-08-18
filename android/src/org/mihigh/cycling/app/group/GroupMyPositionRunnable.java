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
import org.mihigh.cycling.app.group.dto.UserMapDetails;
import org.mihigh.cycling.app.http.HttpHelper;

public class GroupMyPositionRunnable implements Runnable {

    public static final String LOGIN_PATH = "/api/v1/tracking";
    private final double lat;
    private final double lng;
    private final GroupMapFragment fragment;

    public GroupMyPositionRunnable(double lat, double lng, GroupMapFragment fragment) {
        this.lat = lat;
        this.lng = lng;
        this.fragment = fragment;
    }

    @Override
    public void run() {
        String url = fragment.getString(R.string.server_url) + LOGIN_PATH  + "/" + lat + "/" + lng;

        HttpResponse httpResponse = null;
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            httpGet.addHeader("Cookie","SESSION0 = " + HttpHelper.session);
            httpGet.setHeader(HTTP.CONTENT_TYPE, "application/json");

            // Execute HTTP Post Request
            httpResponse = httpclient.execute(httpGet);
        } catch (Exception e) {
            e.printStackTrace();
        }

        BufferedReader reader;
        List<UserMapDetails> usersInfo = null;
        try {
            reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
            String responseBody = reader.readLine();

            Type listType = new TypeToken<ArrayList<UserMapDetails>>() {
            }.getType();

            usersInfo = HttpHelper.getGson().fromJson(responseBody, listType);
        } catch (IOException e) {
            e.printStackTrace();
        }

        fragment.updateAllUsers(usersInfo);
    }



}

