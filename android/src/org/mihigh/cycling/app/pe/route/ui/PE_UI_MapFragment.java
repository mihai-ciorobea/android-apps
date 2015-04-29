package org.mihigh.cycling.app.pe.route.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.pe.route.tracking.gps.PE_GPS_Service;
import org.mihigh.cycling.app.pe.route.ui.utils.MapTrack;

public class PE_UI_MapFragment extends Fragment {

    private ViewPager viewPager;
    private PE_GPS_Service gpsService;
    private GoogleMap map;
    private MapView mapView;

    public PE_UI_MapFragment(ViewPager viewPager, PE_GPS_Service gpsService) {
        this.viewPager = viewPager;
        this.gpsService = gpsService;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.pe_route_activity_started_map, container, false);

        setupMap(savedInstanceState, layout);

        return layout;

    }

    @Override
    public void onStart() {
        super.onStart();

        setupViewPagerButtons();

        MapTrack.setupTrack(mapView, map);
        gpsService.start(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        gpsService.onResume();

    }


    @Override
    public void onPause() {
        super.onPause();
        gpsService.onPause();
    }

    private void setupMap(Bundle savedInstanceState, View layout) {
        MapsInitializer.initialize(getActivity());
        switch (GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity())) {
            case ConnectionResult.SUCCESS:
                mapView = (MapView) layout.findViewById(R.id.pe_route_activity_started_mapView);
                mapView.onCreate(savedInstanceState);
                if (mapView != null) {
                    map = mapView.getMap();
                    map.getUiSettings().setMyLocationButtonEnabled(true);
                    map.setMyLocationEnabled(true);
                    map.setLocationSource(gpsService.locationService);
                }
                break;
            case ConnectionResult.SERVICE_MISSING:
                Toast.makeText(getActivity(), "SERVICE MISSING", Toast.LENGTH_SHORT).show();
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                Toast.makeText(getActivity(), "UPDATE REQUIRED", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(getActivity(), GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity()), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupViewPagerButtons() {
        getView().findViewById(R.id.pe_route_activity_started_map_left_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
            }
        });

        getView().findViewById(R.id.pe_route_activity_started_map_right_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                viewPager.setCurrentItem(2);
            }
        });
    }
}
