package org.mihigh.cycling.app.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

public class LoadingUtils {

    public static ProgressDialog createLoadingDialog(FragmentActivity activity) {
        final ProgressDialog progress = new ProgressDialog(activity);
        progress.setTitle("Loading");
        progress.setMessage("Wait while saving...");
        progress.show();

        return progress;
    }

    public static void makeToast(Context activity, String text) {
        Toast.makeText(activity, text, Toast.LENGTH_LONG).show();
    }
}
