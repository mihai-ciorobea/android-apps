package org.mihigh.cycling.app.pe.group.details;

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
import org.mihigh.cycling.app.pe.group.details.dto.Message;
import org.mihigh.cycling.app.pe.group.dto.PEGroupDetails;

import java.io.IOException;

public class SendMsgRunnable implements Runnable {

    private static final String PATH_CREATE_GROUP = "/api/v1/group/%s/chat";

    private final FragmentActivity activity;
    private final PEHistoryListAdapter adapter;
    private final String text;
    private PEGroupDetails groupDetails;

    public SendMsgRunnable(FragmentActivity activity, PEHistoryListAdapter adapter, String text, PEGroupDetails groupDetails) {
        this.activity = activity;
        this.adapter = adapter;
        this.text = text;
        this.groupDetails = groupDetails;
    }

    @Override
    public void run() {
        String url = activity.getString(R.string.server_url) + String.format(PATH_CREATE_GROUP, groupDetails.id);

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpCall = new HttpPost(url);

            // Auth headers
            httpCall.addHeader("Cookie", Utils.SESSION_ID + " = " + HttpHelper.session);
            httpCall.setHeader(HTTP.CONTENT_TYPE, "application/json");

            // Add text line
            httpCall.setEntity(new StringEntity(text));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httpCall);

            // Check if 202
            if (response.getStatusLine().getStatusCode() > 300) {
                throw new IOException("Received " + response.getStatusLine().getStatusCode());
            }

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.add(new Message() {{
                        this.text = SendMsgRunnable.this.text;
                        this.userInfo = UserInfo.restore(activity);
                    }});
                    adapter.notifyDataSetChanged();
                }
            });
        } catch (Exception e) {
            new ExceptionHandler(activity).sendError(e, false);
        }
    }
}
