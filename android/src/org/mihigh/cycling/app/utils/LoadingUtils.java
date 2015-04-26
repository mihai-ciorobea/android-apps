package org.mihigh.cycling.app.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

public class LoadingUtils {

    public static ProgressDialog createLoadingDialog(FragmentActivity activity) {
        final ProgressDialog progress = new ProgressDialog(activity);
        progress.setTitle("Loading");
        progress.setMessage("Please wait...");
        progress.show();

        return progress;
    }

    public static void makeToast(final Activity activity, final String text) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, text, Toast.LENGTH_LONG).show();
            }
        });
    }
}
