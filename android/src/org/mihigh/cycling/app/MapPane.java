package org.mihigh.cycling.app;

import android.app.Activity;
import android.os.Bundle;

import org.mihigh.cycling.app.filter.ExceptionHandler;

public class MapPane extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        setContentView(R.layout.map);
    }
}
