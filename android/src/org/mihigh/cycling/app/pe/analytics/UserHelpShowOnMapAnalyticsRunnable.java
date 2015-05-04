//package org.mihigh.cycling.app.pe.analytics;
//
//import android.location.Location;
//import android.support.v4.app.FragmentActivity;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.protocol.HTTP;
//import org.mihigh.cycling.app.R;
//import org.mihigh.cycling.app.Utils;
//import org.mihigh.cycling.app.filter.ExceptionHandler;
//import org.mihigh.cycling.app.http.HttpHelper;
//
//import java.io.IOException;
//import java.util.HashMap;
//
//public class UserHelpShowOnMapAnalyticsRunnable implements Runnable {
//
//    private static final String PATH_CREATE_GROUP = "/api/v1/analytics/help/showOnMap";
//    private final FragmentActivity activity;
//    private final Location location;
//
//    public UserHelpShowOnMapAnalyticsRunnable(FragmentActivity activity, Location location) {
//        this.activity = activity;
//        this.location = location;
//    }
//
//
//    @Override
//    public void run() {
//        String url = activity.getString(R.string.server_url) + PATH_CREATE_GROUP;
//
//        try {
//            HttpClient httpclient = new DefaultHttpClient();
//            HttpPost httpCall = new HttpPost(url);
//
//            // Auth headers
//            httpCall.addHeader("Cookie", Utils.SESSION_ID + " = " + HttpHelper.session);
//            httpCall.setHeader(HTTP.CONTENT_TYPE, "application/json");
//
//
//            // Add text line
//            HashMap<String, Object> data = new HashMap<String, Object>();
//            data.put("lat", location.getLatitude());
//            data.put("lng", location.getLongitude());
//            data.put("time", location.getTime());
//            data.put("accuracy", location.getAccuracy());
//            data.put("provider", location.getProvider());
//            httpCall.setEntity(new StringEntity(HttpHelper.getGson().toJson(data)));
//
//            // Execute HTTP Post Request
//            HttpResponse response = httpclient.execute(httpCall);
//
//            // Check if 202
//            if (response.getStatusLine().getStatusCode() > 300) {
//                throw new IOException("Received " + response.getStatusLine().getStatusCode());
//            }
//
//        } catch (Exception e) {
//            new ExceptionHandler(activity).sendError(e, false);
//        }
//    }
//}
