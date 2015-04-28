package org.mihigh.cycling.app.pe.route.collaborative;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.location.Location;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class PECollaborativeLocation {

    public static final String SEPARATOR = "_";
    public static final String SERVICE_BASE_NAME = "BikeRoute" + SEPARATOR;

    public static final String SERVICE_REG_TYPE = "_presence._tcp";

    public final IntentFilter intentFilter = new IntentFilter() {{
        this.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        this.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        this.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        this.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }};

    public WifiP2pManager manager;
    public WifiP2pManager.Channel channel;
    public BroadcastReceiver receiver;
    private WifiP2pDnsSdServiceInfo service;


    public void setup(FragmentActivity activity) {
        manager = (WifiP2pManager) activity.getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(activity, activity.getMainLooper(), null);

        setupDiscoverService();
        discoverServices();
    }

    public void updateLocation(final Location location) {
        // Remove current service if needed
        if (service != null) {
            manager.removeLocalService(channel, service, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {

                    // Create new service with the new location
                    newService(location);
                }

                @Override
                public void onFailure(int reason) {

                }
            });
        } else {
            // Create new service with the new location
            newService(location);
        }
    }

    private void newService(Location location) {
        String serviceName = SERVICE_BASE_NAME + location.getLatitude() + "_" + location.getLongitude();

        setupService(serviceName);
    }

    public void discoverServices() {
        manager.discoverServices(channel, new LoggingActionListener("discoverServices, discoverServices"));
    }


    private void setupService(String serviceName) {
        service = WifiP2pDnsSdServiceInfo.newInstance(serviceName, SERVICE_REG_TYPE, null);
        manager.addLocalService(channel, service, new LoggingActionListener("setupService addLocalService"));
    }

    private void setupDiscoverService() {
        //Register listeners for DNS-SD services. These are callbacks invoked by the system when a service is actually discovered.
        manager.setDnsSdResponseListeners(channel,
                new WifiP2pManager.DnsSdServiceResponseListener() {
                    @Override
                    public void onDnsSdServiceAvailable(String instanceName, String registrationType, WifiP2pDevice srcDevice) {
                        // A service has been discovered. Is this our app?
                        if (instanceName.contains(SERVICE_BASE_NAME)) {
                            Log.d("GIGI", instanceName);
                        }
                    }
                }, null);

        // After attaching listeners, create a service request and initiate discovery.
        WifiP2pDnsSdServiceRequest serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        manager.addServiceRequest(channel, serviceRequest, new LoggingActionListener("setupDiscoverService addServiceRequest"));
    }
}
