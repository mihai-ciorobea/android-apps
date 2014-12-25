package org.mihigh.cycling.app.group;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.Utils;
import org.mihigh.cycling.app.group.dto.ProgressStatus;
import org.mihigh.cycling.app.http.HttpHelper;

public class GroupStartRideRunnable implements Runnable {

    public static final String TRACKING_PATH = "/api/v1/tracking";
    private long id;
    private final GroupHomeFragment fragment;
    private ProgressStatus status;

    public GroupStartRideRunnable(long id, GroupHomeFragment fragment, ProgressStatus status) {
        this.id = id;
        this.fragment = fragment;
        this.status = status;
    }

    @Override
    public void run() {
        try {
            String statusAPI = status == ProgressStatus.ACTIVE ? "start" : "";
            statusAPI = status == ProgressStatus.FINISHED ? "finish" : statusAPI;

            String url = fragment.getString(R.string.server_url) + TRACKING_PATH + "/" + statusAPI + "/" + id;

            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            httpGet.addHeader("Cookie", Utils.SESSION_ID + " = " + HttpHelper.session);
            httpGet.setHeader(HTTP.CONTENT_TYPE, "application/json");

            // Execute HTTP Get Request
            httpclient.execute(httpGet);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}

