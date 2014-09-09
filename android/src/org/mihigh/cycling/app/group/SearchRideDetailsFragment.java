package org.mihigh.cycling.app.group;

import java.text.SimpleDateFormat;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.group.dto.Coordinates;
import org.mihigh.cycling.app.group.dto.JoinedRide;

public class SearchRideDetailsFragment extends Fragment {


    public static final String RIDE = "RIDE_ARG";

    MapView mapView;
    GoogleMap map;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View v = inflater.inflate(R.layout.group_ride_details, container, false);

        try {
            MapsInitializer.initialize(getActivity());
        } catch (Exception e) {
            Log.e("Address Map", "Could not initialize google play", e);
        }

        switch (GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity())) {
            case ConnectionResult.SUCCESS:
                mapView = (MapView) v.findViewById(R.id.ride_map);
                mapView.onCreate(savedInstanceState);
                // Gets to GoogleMap from the MapView and does initialization stuff
                if (mapView != null) {
                    map = mapView.getMap();
                    map.getUiSettings().setZoomControlsEnabled(false);
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
    public void onStart() {
        super.onStart();

        TextView rideName = (TextView) getActivity().findViewById(R.id.ride_name);
        TextView rideDate = (TextView) getActivity().findViewById(R.id.ride_date);
        TextView rideStatus = (TextView) getActivity().findViewById(R.id.ride_status);
        Button joinButton = (Button) getActivity().findViewById(R.id.start_ride);



        Bundle args = getArguments();
        if (args != null) {
            final JoinedRide joinedRide = (JoinedRide) args.getSerializable(RIDE);
            rideName.setText(joinedRide.name);
            rideDate.setText(new SimpleDateFormat("dd-MM-yyyy HH:mm").format(joinedRide.startDate));

            //Show StartRide button
                    joinButton.setVisibility(View.VISIBLE);

                    joinButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new Thread(new JoinedActivityRunnable(SearchRideDetailsFragment.this, joinedRide.id)).start();
                        }
                    });

            List<Coordinates> track = joinedRide.coordinates;
            PolylineOptions polylineOptions = new PolylineOptions();

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Coordinates mark : track) {
                LatLng point = new LatLng(mark.getLatitude(), mark.getLongitude());
                polylineOptions.add(point);
                builder.include(point);
            }
            map.addPolyline(polylineOptions);

            map.addMarker(new MarkerOptions()
                              .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                              .position(new LatLng(track.get(0).getLatitude(), track.get(0).getLongitude())));

            LatLngBounds bounds = builder.build();
            int padding = 20; // offset from edges of the map in pixels
            final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

            mapView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // If you need this to be called again, then run again addOnGlobalLayoutListener.
                        mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        map.animateCamera(cu);
                    }
                });
        }


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

    public void updateHomeView() {
        //TODO:
    }


}
