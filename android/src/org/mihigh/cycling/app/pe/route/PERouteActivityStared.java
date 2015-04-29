package org.mihigh.cycling.app.pe.route;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.pe.route.tracking.LocationTracking;
import org.mihigh.cycling.app.pe.route.tracking.collaborative.PECollaborativeLocation;
import org.mihigh.cycling.app.pe.route.tracking.gps.PE_GPS_Service;
import org.mihigh.cycling.app.pe.route.ui.PE_UI_TracingViewPageAdapter;

public class PERouteActivityStared extends Fragment {

    public static Activity activity;

    private PECollaborativeLocation collaborativeLocation = new PECollaborativeLocation();
    private PE_GPS_Service gpsService = new PE_GPS_Service();
    private LocationTracking locationTracking = new LocationTracking(collaborativeLocation, gpsService);


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        return inflater.inflate(R.layout.pe_route_activity_started, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        activity = getActivity();

        // Create wifi p2p setup
        collaborativeLocation.setup(getActivity());

        setupUIComponents();
//        locationTracking.startTracking();
    }

    private void setupUIComponents() {

        // Setup viewPager. Set default page the map one
        ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.pe_route_activity_stated_viewPager);
        PE_UI_TracingViewPageAdapter viewPageAdapter = new PE_UI_TracingViewPageAdapter(getChildFragmentManager(), viewPager, gpsService);
        viewPager.setAdapter(viewPageAdapter);
        viewPager.setCurrentItem(viewPageAdapter.getMapIndex());
    }

    @Override
    public void onStop() {
        collaborativeLocation.onStop();
        super.onStop();
    }


    @Override
    public void onResume() {
        super.onResume();
        collaborativeLocation.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        collaborativeLocation.onPause();
    }

}



/*
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
 */