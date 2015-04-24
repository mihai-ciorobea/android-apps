package org.mihigh.cycling.app.utils;

import android.app.ProgressDialog;
import android.support.v4.app.FragmentActivity;

public class LoadingUtils {

    public static ProgressDialog createLoadingDialog(FragmentActivity activity) {
        final ProgressDialog progress = new ProgressDialog(activity);
        progress.setTitle("Loading");
        progress.setMessage("Wait while saving...");
        progress.show();

        return progress;
    }
}
