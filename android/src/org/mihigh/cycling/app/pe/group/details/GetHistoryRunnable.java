package org.mihigh.cycling.app.pe.group.details;

import android.app.ProgressDialog;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
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
import org.mihigh.cycling.app.pe.group.details.dto.Message;
import org.mihigh.cycling.app.pe.group.dto.PEGroupDetails;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class GetHistoryRunnable implements Runnable {

    private static final String PATH_CREATE_GROUP = "/api/v1/message/group/%s";

    private final FragmentActivity activity;
    private final PEHistoryListAdapter adapter;
    private final TextView noneTextView;
    private PEGroupDetails groupDetails;
    private ProgressDialog loadingDialog;
    private ListView historyList;

    public GetHistoryRunnable(FragmentActivity activity, PEHistoryListAdapter adapter, TextView noneTextView,
                              PEGroupDetails groupDetails, ProgressDialog loadingDialog, ListView historyList) {
        this.activity = activity;
        this.adapter = adapter;
        this.noneTextView = noneTextView;
        this.groupDetails = groupDetails;
        this.loadingDialog = loadingDialog;
        this.historyList = historyList;
    }

    @Override
    public void run() {
        String url = activity.getString(R.string.server_url) + String.format(PATH_CREATE_GROUP, groupDetails.id);

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

            Type type = new TypeToken<List<Message>>() {
            }.getType();
            final List<Message> allMessages = HttpHelper.fromInputStream(response.getEntity().getContent(), type);

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (allMessages == null || allMessages.isEmpty()) {
                        noneTextView.setVisibility(View.VISIBLE);
                        historyList.setVisibility(View.GONE);
                        return;
                    }

                    for (Message msg : allMessages) {
                        adapter.add(msg);
                    }
                    adapter.notifyDataSetChanged();
                    historyList.setSelection(adapter.getCount() - 1);

                }
            });
        } catch (Exception e) {
            new ExceptionHandler(activity).sendError(e, false);
        }

        loadingDialog.dismiss();
    }
}
