package org.mihigh.cycling.app.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.mihigh.cycling.app.login.dto.UserInfo;

public class HttpHelper {

    public static String session;
    private static Gson gson;

    public static Gson getGson() {

        if (gson == null) {
            GsonBuilder builder = new GsonBuilder();
            builder.setDateFormat("yyyy/MM/dd");
            gson = builder.create();
        }

        return gson;
    }
}
