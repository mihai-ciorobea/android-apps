package org.mihigh.cycling.app.pe.route;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import org.mihigh.cycling.app.group.dto.Coordinates;
import org.mihigh.cycling.app.http.HttpHelper;
import org.mihigh.cycling.app.login.dto.UserInfo;
import org.mihigh.cycling.app.pe.route.ui.PE_UI_MapFragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GetUserHelpRunnable implements Runnable {

    private static final String PATH_CREATE_GROUP = "/api/v1/activity/PE/help";
    private final FragmentActivity activity;
    private PE_UI_MapFragment map;

    public GetUserHelpRunnable(FragmentActivity activity, PE_UI_MapFragment map) {
        this.activity = activity;
        this.map = map;
    }


    @Override
    public void run() {
        String url = activity.getString(R.string.server_url) + PATH_CREATE_GROUP;

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpCall = new HttpGet(url);

            // Auth headers
            httpCall.addHeader("Cookie", Utils.SESSION_ID + " = " + HttpHelper.session);
            httpCall.setHeader(HTTP.CONTENT_TYPE, "application/json");


            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httpCall);

            // Check if response
            if (response.getStatusLine().getStatusCode() > 300) {
                throw new IOException("Received " + response.getStatusLine().getStatusCode());
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            String responseBody = reader.readLine();

            Type listType = new TypeToken<List<HelpRequest>>() {
            }.getType();

            final List<HelpRequest> helpRequests = HttpHelper.getGson().fromJson(responseBody, listType);

            final List<AlertDialog.Builder> allAlerts = new ArrayList<AlertDialog.Builder>();
            for (final HelpRequest helpRequest : helpRequests) {
                AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                allAlerts.add(alert);

                final int index = helpRequests.indexOf(helpRequest);
                alert.setTitle(helpRequest.userInfo.getFirstName() + " " + helpRequest.userInfo.getLastName());
                alert.setMessage("Need help: " + helpRequest.text);

                alert.setPositiveButton("Show on map", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        map.openMarker(helpRequest.userInfo, helpRequest.lastLocation);
                    }
                });

                alert.setNegativeButton("Cancel All", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

                alert.setNeutralButton("Next", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        AlertDialog.Builder alert = allAlerts.size() == index + 1 ? null : allAlerts.get(index + 1);
                        if (alert != null) {
                            alert.show();
                        }
                    }
                });
            }

            if (allAlerts.get(0) != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        allAlerts.get(0).show();
                    }
                });
            }

        } catch (Exception e) {
            new ExceptionHandler(activity).sendError(e, false);
        }
    }
}


class HelpRequest {
    public UserInfo userInfo;
    public Coordinates lastLocation;
    public String text;
}