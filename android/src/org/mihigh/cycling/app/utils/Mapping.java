package org.mihigh.cycling.app.utils;

import org.mihigh.cycling.app.http.HttpHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Mapping {

    public static <T> T map(InputStream content, Class<T> type) throws IOException {

        BufferedReader reader;
        reader = new BufferedReader(new InputStreamReader(content, "UTF-8"));
        String responseBody = reader.readLine();

        return HttpHelper.getGson().fromJson(responseBody, type);
    }

}
