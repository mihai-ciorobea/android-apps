package org.mihigh.cycling.app.filter;

import android.app.Activity;
import android.content.Intent;

import org.mihigh.cycling.app.LoginActivity;

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
}
