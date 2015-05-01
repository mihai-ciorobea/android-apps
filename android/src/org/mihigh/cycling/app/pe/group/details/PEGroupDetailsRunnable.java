package org.mihigh.cycling.app.pe.group.details;

import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.Utils;
import org.mihigh.cycling.app.filter.ExceptionHandler;
import org.mihigh.cycling.app.http.HttpHelper;
import org.mihigh.cycling.app.pe.group.get.PEUserGroups;

import java.io.IOException;
import java.lang.reflect.Type;

public class PEGroupDetailsRunnable implements Runnable {
    private static final String PATH_CREATE_GROUP = "/api/v1/group?state=joined";
    private final PEGroupHome fragment;

    public PEGroupDetailsRunnable(PEGroupHome fragment) {
        this.fragment = fragment;
    }


    @Override
    public void run() {
        String url = fragment.getString(R.string.server_url) + PATH_CREATE_GROUP;

        try {
            HttpGet httpCall = new HttpGet(url);

            // Auth headers
            httpCall.addHeader("Cookie", Utils.SESSION_ID + " = " + HttpHelper.session);
            httpCall.setHeader(HTTP.CONTENT_TYPE, "application/json");

            // Execute HTTP Post Request
            HttpResponse response = new DefaultHttpClient().execute(httpCall);

            // Check if 200
            if (response.getStatusLine().getStatusCode() > 300) {
                throw new IOException("Received " + response.getStatusLine().getStatusCode());
            }

            // all good

            // deserialize
            Type type = new TypeToken<PEUserGroups>() {
            }.getType();
            final PEUserGroups groups = HttpHelper.fromInputStream(response.getEntity().getContent(), type);
            if (groups != null
                    && groups.groups != null
                    && !groups.groups.isEmpty()) {

                fragment.getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        fragment.updateDetails(groups.groups.get(0));
                    }
                });
            }
        } catch (Exception e) {
            new ExceptionHandler(fragment.getActivity()).sendError(e, false);
        }

    }
}
