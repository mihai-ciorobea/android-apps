package org.mihigh.cycling.app.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

public class HttpHelper {

    public static String session;
    private static Gson gson;

    public synchronized static Gson getGson() {

        if (gson == null) {
            GsonBuilder builder = new GsonBuilder();
            builder.setDateFormat("yyyy/MM/dd");
            gson = builder.create();
        }

        return gson;
    }


    public static <T> T fromInputStream(InputStream content) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(content, "UTF-8"));
        String responseBody = reader.readLine();

        Type type = new TypeToken<T>() {
        }.getType();

        return HttpHelper.getGson().fromJson(responseBody, type);
    }
}
