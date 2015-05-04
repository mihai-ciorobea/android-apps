package org.mihigh.cycling.app.pe.route;

import android.location.Location;
import android.support.v4.app.FragmentActivity;
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

import java.io.IOException;
import java.util.*;

public class SendUserPositionRunnable implements Runnable {

    final static List<SendUserPositionRunnable> failedUpdates = Collections.synchronizedList(new ArrayList<SendUserPositionRunnable>());


    private static final String PATH_CREATE_GROUP = "/api/v1/activity/1/location";
    private FragmentActivity activity;
    private final Location location;

    public SendUserPositionRunnable(FragmentActivity activity, Location location) {
        this.activity = activity;
        this.location = location;
    }


    @Override
    public void run() {

        boolean succeeded = sendLocation();

        synchronized (failedUpdates) {
            if (succeeded) {
                //try others
                for (Iterator<SendUserPositionRunnable> iterator = failedUpdates.iterator(); iterator.hasNext(); ) {
                    SendUserPositionRunnable failedUpdate = iterator.next();
                    failedUpdate.activity = activity;
                    boolean retryStatus = failedUpdate.sendLocation();
                    if (!retryStatus) {
                        break;
                    } else {
                        iterator.remove();
                    }
                }
            } else {
                //add to failed
                failedUpdates.add(this);
            }
        }

    }

    private boolean sendLocation() {
        String url = activity.getString(R.string.server_url) + PATH_CREATE_GROUP;

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpCall = new HttpPost(url);

            // Auth headers
            httpCall.addHeader("Cookie", Utils.SESSION_ID + " = " + HttpHelper.session);
            httpCall.setHeader(HTTP.CONTENT_TYPE, "application/json");
            httpCall.setHeader(Utils.EMAIL, UserInfo.restore(activity) == null ? null : UserInfo.restore(activity).getEmail());


            // Add text line
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("lat", location.getLatitude());
            data.put("lng", location.getLongitude());
            data.put("time", location.getTime());
            data.put("accuracy", location.getAccuracy());
            data.put("provider", location.getProvider());
            data.put("battery", PERouteActivityStared.battery);
            httpCall.setEntity(new StringEntity(HttpHelper.getGson().toJson(data)));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httpCall);

            // Check if 202
            if (response.getStatusLine().getStatusCode() > 300) {
                throw new IOException("Received " + response.getStatusLine().getStatusCode());
            }

        } catch (Exception e) {
            new ExceptionHandler(activity).sendError(e, false);
            return false;
        }
        return true;
    }
}
