package org.mihigh.cycling.app.group;

import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.Utils;
import org.mihigh.cycling.app.filter.ExceptionHandler;
import org.mihigh.cycling.app.group.dto.Coordinates;
import org.mihigh.cycling.app.group.dto.UserMapDetails;
import org.mihigh.cycling.app.http.HttpHelper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GroupMyPositionRunnable implements Runnable {

    public static final String TRACKING_PATH = "/api/v1/tracking";
    private final long date;
    private long id;
    private final double lat;
    private final double lng;
    private final GroupMapFragment fragment;

    private static final ArrayList<GroupMyPositionRunnable> old = new ArrayList<GroupMyPositionRunnable>();


    public GroupMyPositionRunnable(long id, double lat, double lng, GroupMapFragment fragment) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
        this.fragment = fragment;
        this.date = new Date().getTime();
    }

    @Override
    public void run() {
        execute();

        ArrayList<GroupMyPositionRunnable> retry = new ArrayList<GroupMyPositionRunnable>(old);
        old.clear();

        boolean sendHttpCall = true;
        for (GroupMyPositionRunnable oldItem : retry) {
            if (oldItem != null) {
                if (sendHttpCall == true) {
                    sendHttpCall = oldItem.execute();
                } else {
                    old.add(oldItem);
                }
            }
        }
    }

    private boolean execute() {
        try {
            String url = fragment.getString(R.string.server_url) + TRACKING_PATH + "/position/" + id;

            HttpResponse httpResponse;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Cookie", Utils.SESSION_ID + " = " + HttpHelper.session);
            httpPost.setHeader(HTTP.CONTENT_TYPE, "application/json");

            StringEntity entity = new StringEntity(HttpHelper.getGson().toJson(new Coordinates(lat, lng, date)));
            httpPost.setEntity(entity);

            // Execute HTTP Post Request
            httpResponse = httpclient.execute(httpPost);

            if (httpResponse.getStatusLine().getStatusCode() == 401) {

            }

            BufferedReader reader;
            List<UserMapDetails> usersInfo;
            reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
            String responseBody = reader.readLine();

            Type listType = new TypeToken<ArrayList<UserMapDetails>>() {
            }.getType();

            usersInfo = HttpHelper.getGson().fromJson(responseBody, listType);
            fragment.updateAllUsers(usersInfo);
            return true;
        } catch (Throwable e) {
            new ExceptionHandler(fragment.getActivity()).sendError(e, false);
            old.add(this);
            return false;
        }
    }
}

