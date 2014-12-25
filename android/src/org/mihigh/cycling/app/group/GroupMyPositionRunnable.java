package org.mihigh.cycling.app.group;

import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.Utils;
import org.mihigh.cycling.app.group.dto.Coordinates;
import org.mihigh.cycling.app.group.dto.UserMapDetails;
import org.mihigh.cycling.app.http.HttpHelper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GroupMyPositionRunnable implements Runnable {

  public static final String TRACKING_PATH = "/api/v1/tracking";
  private long id;
  private final double lat;
  private final double lng;
  private final GroupMapFragment fragment;

  public GroupMyPositionRunnable(long id, double lat, double lng, GroupMapFragment fragment) {
    this.id = id;
    this.lat = lat;
    this.lng = lng;
    this.fragment = fragment;
  }

  @Override
  public void run() {
    try {
      String url = fragment.getString(R.string.server_url) + TRACKING_PATH + "/position/" + id;

      HttpResponse httpResponse;
      HttpClient httpclient = new DefaultHttpClient();
      HttpPost httpPost = new HttpPost(url);
      httpPost.addHeader("Cookie", Utils.SESSION_ID + " = " + HttpHelper.session);
      httpPost.setHeader(HTTP.CONTENT_TYPE, "application/json");

      StringEntity entity = new StringEntity(HttpHelper.getGson().toJson(new Coordinates(lat, lng)));
      httpPost.setEntity(entity);

      // Execute HTTP Post Request
      httpResponse = httpclient.execute(httpPost);

      BufferedReader reader;
      List<UserMapDetails> usersInfo = null;
      reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
      String responseBody = reader.readLine();

      Type listType = new TypeToken<ArrayList<UserMapDetails>>() {}.getType();

      usersInfo = HttpHelper.getGson().fromJson(responseBody, listType);
      fragment.updateAllUsers(usersInfo);
    } catch (Throwable e) {
        throw new RuntimeException(e);
    }
  }
}

