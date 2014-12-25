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
        StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));
        StringBuilder errorReport = new StringBuilder();
        errorReport.append("************ CAUSE OF ERROR ************\n\n");
        errorReport.append(stackTrace.toString());

//        errorReport.append("\n************ DEVICE INFORMATION ***********\n");
//        errorReport.append("Brand: ");
//        errorReport.append(Build.BRAND);
//        errorReport.append(LINE_SEPARATOR);
//        errorReport.append("Device: ");
//        errorReport.append(Build.DEVICE);
//        errorReport.append(LINE_SEPARATOR);
//        errorReport.append("Model: ");
//        errorReport.append(Build.MODEL);
//        errorReport.append(LINE_SEPARATOR);
//        errorReport.append("Id: ");
//        errorReport.append(Build.ID);
//        errorReport.append(LINE_SEPARATOR);
//        errorReport.append("Product: ");
//        errorReport.append(Build.PRODUCT);
//        errorReport.append(LINE_SEPARATOR);
//        errorReport.append("\n************ FIRMWARE ************\n");
//        errorReport.append("SDK_INT: ");
//        errorReport.append(Build.VERSION.SDK_INT);
//        errorReport.append(LINE_SEPARATOR);
//        errorReport.append("Release: ");
//        errorReport.append(Build.VERSION.RELEASE);
//        errorReport.append(LINE_SEPARATOR);
//        errorReport.append("Incremental: ");
//        errorReport.append(Build.VERSION.INCREMENTAL);
//        errorReport.append(LINE_SEPARATOR);
//        errorReport.append("activity: ");
//        errorReport.append(activity.toString());
//        errorReport.append(LINE_SEPARATOR);

        System.err.println(errorReport.toString());
        new Thread(new SendErrorRunnable(activity, errorReport.toString(), android.os.Process.myPid())).start();

        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);


//        System.exit(10);
    }
}
