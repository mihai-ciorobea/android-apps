package org.mihigh.cycling.app.solo;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

public class SaveRideTask extends AsyncTask<String, Void, HttpResponse> {

    private Exception exception;
    private int statusCode;

    @Override
    protected HttpResponse doInBackground(String... jsonData) {
        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://www.yoursite.com/script.php");

            // Add your data
            httppost.setEntity(new StringEntity(jsonData[0]));

            // Execute HTTP Post Request
            return httpclient.execute(httppost);

        } catch (Exception e) {
            this.exception = e;
        }
        return null;
    }

    protected void onPostExecute(HttpResponse httpResponse) {
        if (exception != null) {
            //TODO: handle error
        }


        //TODO: check if ok
        this.statusCode = httpResponse.getStatusLine().getStatusCode();
    }

    public int getStatusCode() {
        return statusCode;
    }
}