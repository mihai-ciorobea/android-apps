package org.mihigh.cycling.app.pe.group.details.settings;

import android.app.ProgressDialog;
import android.support.v4.app.FragmentActivity;
import android.widget.ArrayAdapter;
import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
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
import org.mihigh.cycling.app.utils.LoadingUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class GetGroupMembersRunnable implements Runnable {
    private static final String PATH_CREATE_GROUP = "/api/v1/group/%s/members";

    private FragmentActivity activity;
    private PEGroupDetails groupDetails;
    private ArrayAdapter<String> membersAdapter;
    private ProgressDialog loadingDialog;


    public GetGroupMembersRunnable(FragmentActivity activity, PEGroupDetails groupDetails,
                                   ArrayAdapter<String> membersAdapter, ProgressDialog loadingDialog) {
        this.activity = activity;
        this.groupDetails = groupDetails;
        this.membersAdapter = membersAdapter;
        this.loadingDialog = loadingDialog;
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

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httpCall);

            // Check if 200
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new IOException("Received " + response.getStatusLine().getStatusCode());
            }

            Type type = new TypeToken<List<UserInfo>>() {
            }.getType();
            final List<UserInfo> users = HttpHelper.fromInputStream(response.getEntity().getContent(), type);
            if (users != null
                    && !users.isEmpty()) {

                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        for (UserInfo user : users) {
                            membersAdapter.add(user.isGenerated() ?
                                    user.getEmail().split("@")[0] :
                                    user.getFirstName() + " " + user.getLastName());
                        }

                        membersAdapter.notifyDataSetChanged();
                    }
                });
            }
        } catch (Exception e) {
            new ExceptionHandler(activity).sendError(e, false);
            LoadingUtils.makeToast(activity, "Try again..");
        }

        loadingDialog.dismiss();
    }
}
