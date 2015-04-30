package org.mihigh.cycling.app.filter;

import android.app.Activity;
import android.content.Intent;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.mihigh.cycling.app.LoginActivity;
import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.Utils;
import org.mihigh.cycling.app.http.HttpHelper;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final String LINE_SEPARATOR = "\n";
    private Activity activity;

    public ExceptionHandler(Activity activity) {
        this.activity = activity;
    }


    @Override
    public void uncaughtException(Thread thread, Throwable exception) {
        sendError(exception, true);

        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
    }

    public void sendError(Throwable exception, boolean kill) {
        StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));
        StringBuilder errorReport = new StringBuilder();
        errorReport.append("************ CAUSE OF ERROR ************\n\n");
        errorReport.append(stackTrace.toString());

        System.err.println(errorReport.toString());
        new Thread(new SendErrorRunnable(activity, errorReport.toString(), kill, android.os.Process.myPid())).start();
    }

    public void sendLog(Throwable exception, boolean kill) {
        StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));
        StringBuilder errorReport = new StringBuilder();
        errorReport.append("************ CAUSE OF ERROR ************\n\n");
        errorReport.append(stackTrace.toString());

        System.err.println(errorReport.toString());
        new Thread(new SendLogRunnable(activity, errorReport.toString(), kill, android.os.Process.myPid())).start();
    }
}

//TODO DELTE ME
class SendLogRunnable implements Runnable {

    public static final String PATH = "/api/v1/log";
    private final Activity activity;
    private String errorData;


    public SendLogRunnable(Activity activity, String errorData, boolean kill, int pid) {
        this.activity = activity;
        this.errorData = errorData;
    }

    @Override
    public void run() {
        execute();
    }


    private boolean execute() {
        String url = activity.getString(R.string.server_url) + PATH;

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            httppost.addHeader("Cookie", Utils.SESSION_ID + " = " + HttpHelper.session);
            httppost.setHeader(HTTP.CONTENT_TYPE, "application/json");

            // Add your data
            httppost.setEntity(new StringEntity(errorData));

            // Execute HTTP Post Request
            httpclient.execute(httppost);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}