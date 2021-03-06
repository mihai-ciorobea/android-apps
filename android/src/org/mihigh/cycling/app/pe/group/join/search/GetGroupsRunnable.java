package org.mihigh.cycling.app.pe.group.join.search;

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
import org.mihigh.cycling.app.pe.group.dto.PEGroupDetails;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class GetGroupsRunnable implements Runnable {

    private static final String PATH_CREATE_GROUP = "/api/v1/group?filter=%s";


    private final FragmentActivity activity;
    private final PESearchGroupListAdapter adapter;
    private final String searchText;

    public GetGroupsRunnable(FragmentActivity activity, PESearchGroupListAdapter adapter, String searchText) {

        this.activity = activity;
        this.adapter = adapter;
        this.searchText = searchText;
    }

    @Override
    public void run() {
        String url = activity.getString(R.string.server_url) + String.format(PATH_CREATE_GROUP, searchText);

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

            Type type = new TypeToken<List<PEGroupDetails>>() {
            }.getType();
            final List<PEGroupDetails> groups = HttpHelper.fromInputStream(response.getEntity().getContent(), type);

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.clear();
                    if (groups == null
                            || groups.isEmpty()) {
                        return;
                    }

                    for (PEGroupDetails group : groups) {
                        adapter.add(group);
                    }
                    adapter.notifyDataSetChanged();
                }
            });
        } catch (Exception e) {
            new ExceptionHandler(activity).sendError(e, false);
        }


    }
}
