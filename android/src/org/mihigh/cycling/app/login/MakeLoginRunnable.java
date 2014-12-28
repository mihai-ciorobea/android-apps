package org.mihigh.cycling.app.login;

import android.widget.Toast;

import com.google.gson.Gson;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.mihigh.cycling.app.LoginActivity;
import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.Utils;
import org.mihigh.cycling.app.http.HttpHelper;
import org.mihigh.cycling.app.login.dto.UserInfo;

public class MakeLoginRunnable implements Runnable {

    public static final String LOGIN_PATH = "/api/v1/login";
    private final UserInfo userInfo;
    private final LoginActivity activity;

    public MakeLoginRunnable(UserInfo userInfo, LoginActivity activity) {
        this.userInfo = userInfo;
        this.activity = activity;
    }

    @Override
    public void run() {
        String url = activity.getString(R.string.server_url) + LOGIN_PATH;

        Gson gson = new Gson();
        String jsonUserInfo = gson.toJson(userInfo);

        HttpResponse httpResponse;
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);

            httppost.setHeader(HTTP.CONTENT_TYPE, "application/json");

            // Add your data
            StringEntity entity = new StringEntity(jsonUserInfo);
            httppost.setEntity(entity);

            // Execute HTTP Post Request
            httpResponse = httpclient.execute(httppost);
        } catch (Exception e) {
            Toast.makeText(activity, "Check internet connection!", Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        }

        Header[] headers = httpResponse.getHeaders("Set-Cookie");
        for (Header h : headers) {
            if (h.getName().equalsIgnoreCase("Set-Cookie")) {
                 for(HeaderElement cookie: h.getElements()) {
                     if (cookie.getName().equalsIgnoreCase(Utils.SESSION_ID + "")) {
                         HttpHelper.session = cookie.getValue();
                     }
                 }
            }
        }

        activity.updateUserInfo(userInfo);
        activity.onUserLoggedIn();
    }
}

