package org.mihigh.cycling.app.pe.group.details;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;
import org.mihigh.cycling.app.filter.ExceptionHandler;

import java.io.InputStream;

public class DownloadImageRunnable implements Runnable {
    private FragmentActivity activity;
    ImageView bmImage;
    String url;

    public DownloadImageRunnable(FragmentActivity activity, ImageView bmImage, String url) {
        this.activity = activity;
        this.bmImage = bmImage;
        this.url = url;
    }

    @Override
    public void run() {
        try {
            InputStream in = new java.net.URL(url).openConnection().getInputStream();
            final Bitmap bm = BitmapFactory.decodeStream(in);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bmImage.setImageBitmap(bm);
                }
            });
        } catch (Exception e) {
            new ExceptionHandler(activity).sendError(e, false);
        }
    }
}
