package org.mihigh.cycling.app.pe.route.collaborative;

import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

public class LoggingActionListener implements WifiP2pManager.ActionListener {

    private String action;

    public LoggingActionListener(String action) {
        this.action = action;
    }

    @Override
    public void onSuccess() {
        Log.d("BikeRoute LoggingActionListener", action + " onSuccess");
    }

    @Override
    public void onFailure(int reason) {
        Log.d("BikeRoute LoggingActionListener", action + " onFailure " + reason);
    }
}
