package org.mihigh.cycling.app.pe.group.dto;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.Utils;
import org.mihigh.cycling.app.http.HttpHelper;
import org.mihigh.cycling.app.login.dto.UserInfo;
import org.mihigh.cycling.app.pe.PEHome;
import org.mihigh.cycling.app.pe.group.get.PEUserGroups;

import java.io.IOException;

public class PECheckGroupForUser implements Runnable {
    private static final String PATH_CREATE_GROUP = "/api/v1/user/%s/groups";
    private final UserInfo userInfo;
    private final PEHome fragment;

    public PECheckGroupForUser(UserInfo userInfo, PEHome fragment) {

        this.userInfo = userInfo;
        this.fragment = fragment;
    }


    @Override
    public void run() {
        String url = fragment.getString(R.string.server_url) + String.format(PATH_CREATE_GROUP, userInfo.getEmail());

        try {
            HttpGet httpCall = new HttpGet(url);

            // Auth headers
            httpCall.addHeader("Cookie", Utils.SESSION_ID + " = " + HttpHelper.session);
            httpCall.setHeader(HTTP.CONTENT_TYPE, "application/json");

            // Execute HTTP Post Request
            HttpResponse response = new DefaultHttpClient().execute(httpCall);

            // Check if 200
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new IOException("Received " + response.getStatusLine().getStatusCode());
            }

            // all good

            // deserialize
            PEUserGroups groups = HttpHelper.fromInputStream(response.getEntity().getContent());
            if (groups != null
                    && groups.groups != null
                    && groups.groups.isEmpty()) {
                groups.groups.get(0).setHasGroup(fragment.getActivity(), true);

                fragment.getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        fragment.updateHasGroup();
                    }
                });
            }
        } catch (Exception ignored) {
        }

    }
}
