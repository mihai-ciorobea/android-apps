package org.mihigh.cycling.app.pe.route;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.pe.route.collaborative.LoggingActionListener;
import org.mihigh.cycling.app.pe.route.collaborative.PECollaborativeLocation;

public class PEActivityStared extends Fragment {

//    public static final String TAG = "wifip2p";
//
//    public static final String TXTRECORD_PROP_AVAILABLE = "available";
//    public static final String SERVICE_BASE_NAME = "BikeRoute";
//    public static final String SERVICE_REG_TYPE = "_presence._tcp";
//
//    private WifiP2pManager manager;
//
//    private final IntentFilter intentFilter = new IntentFilter();
//    private WifiP2pManager.Channel channel;
//    private BroadcastReceiver receiver = null;
//    private WifiP2pDnsSdServiceInfo service;
//    private String serviceName = SERVICE_BASE_NAME;
//

    PECollaborativeLocation collaborativeLocation = new PECollaborativeLocation();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        return inflater.inflate(R.layout.pe_activity_stared, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();


        collaborativeLocation.setup(getActivity());
        collaborativeLocation.updateLocation(new Location((String) null) {
            @Override
            public double getLongitude() {
                return 10;
            }

            @Override
            public double getLatitude() {
                return 11;
            }
        });


        getView().findViewById(R.id.pe_activity_started_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collaborativeLocation.discoverServices();
            }
        });

    }

    @Override
    public void onStop() {
        collaborativeLocation.manager.removeGroup(collaborativeLocation.channel, new LoggingActionListener("removeGroup"));
        super.onStop();
    }


    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(collaborativeLocation.receiver, collaborativeLocation.intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(collaborativeLocation.receiver);
    }

}
