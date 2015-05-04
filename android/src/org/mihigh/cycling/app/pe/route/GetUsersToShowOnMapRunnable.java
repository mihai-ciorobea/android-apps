package org.mihigh.cycling.app.pe.route;

import android.location.Location;
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
import org.mihigh.cycling.app.pe.route.ui.PE_UI_MapFragment;
import org.mihigh.cycling.app.pe.route.ui.dto.PEUserMapDetails;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

public class GetUsersToShowOnMapRunnable implements Runnable {

    private static final String PATH_CREATE_GROUP = "/api/v1/activity/1/users?group=%s&nearby=%s";
    private final FragmentActivity activity;
    private final Location location;
    private final boolean setGroupVisibility;
    private final boolean setNearbyVisibility;
    private PE_UI_MapFragment map;

    public GetUsersToShowOnMapRunnable(FragmentActivity activity, Location location, boolean setGroupVisibility,
                                       boolean setNearbyVisibility, PE_UI_MapFragment map) {
        this.activity = activity;
        this.location = location;
        this.setGroupVisibility = setGroupVisibility;
        this.setNearbyVisibility = setNearbyVisibility;
        this.map = map;
    }


    @Override
    public void run() {
        String url = activity.getString(R.string.server_url) + String.format(PATH_CREATE_GROUP, setGroupVisibility, setNearbyVisibility);

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


            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            String responseBody = reader.readLine();

            Type listType = new TypeToken<List<PEUserMapDetails>>() {
            }.getType();

            final List<PEUserMapDetails> users = HttpHelper.getGson().fromJson(responseBody, listType);

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    map.updateUserPositions(users);
                }
            });


        } catch (Exception e) {
            new ExceptionHandler(activity).sendError(e, false);
        }
    }
}
