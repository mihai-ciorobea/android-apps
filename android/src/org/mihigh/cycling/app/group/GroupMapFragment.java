package org.mihigh.cycling.app.group;

import java.util.List;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.group.dto.UserMapDetails;

public class GroupMapFragment extends Fragment {


    MapView mapView;


    GoogleMap map;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.group_map, container, false);

        try {
            MapsInitializer.initialize(getActivity());
        } catch (Exception e) {
            Log.e("Address Map", "Could not initialize google play", e);
        }

        switch (GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity())) {
            case ConnectionResult.SUCCESS:
                mapView = (MapView) v.findViewById(R.id.group_map);
                mapView.onCreate(savedInstanceState);
                // Gets to GoogleMap from the MapView and does initialization stuff
                if (mapView != null) {
                    map = mapView.getMap();
                    map.setMyLocationEnabled(true);
                    map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

                        @Override
                        public void onMyLocationChange(Location location) {
                            GroupTracking.instance.addLocation(location);
                            new Thread(new GroupMyPositionRunnable(location.getLatitude(), location.getLongitude(), GroupMapFragment.this)).start();
                        }
                    });
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

        return v;
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    public void updateAllUsers(final List<UserMapDetails> usersInfo) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                map.clear();
                for (UserMapDetails userInfo : usersInfo) {
                    map.addMarker(new MarkerOptions()
                                      .position(new LatLng(userInfo.lat, userInfo.lng))
                                      .title(userInfo.email)
                                      .snippet(String.valueOf(usersInfo.indexOf(userInfo))));


                    //TODO: keep window open even on refresh of data
                    map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                        @Override
                        public View getInfoWindow(Marker arg0) {
                            return null;
                        }

                        @Override
                        public View getInfoContents(Marker marker) {
                            View v = getActivity().getLayoutInflater().inflate(R.layout.group_map_window_adapter, null);
                            TextView distance = (TextView) v.findViewById(R.id.distance);
                            TextView time = (TextView) v.findViewById(R.id.time);

                            UserMapDetails userInfo = usersInfo.get(Integer.parseInt(marker.getSnippet()));

                            distance.setText("Distance in meters: " + userInfo.distanceInMeters);
                            time.setText("Distance in time: " + userInfo.distanceInTime);

                            return v;
                        }
                    });
                }
            }
        });
    }
}
