package org.mihigh.cycling.app.pe.route.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.filter.ExceptionHandler;
import org.mihigh.cycling.app.group.dto.Coordinates;
import org.mihigh.cycling.app.pe.route.PETrackCoordinates;
import org.mihigh.cycling.app.pe.route.tracking.UserTracking;
import org.mihigh.cycling.app.pe.route.tracking.gps.PE_GPS_Service;
import org.mihigh.cycling.app.pe.route.ui.dto.PEUserMapDetails;
import org.mihigh.cycling.app.pe.route.ui.utils.MapTrack;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        gpsService.onDestroy();
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


    ConcurrentHashMap<String, Marker> markers = new ConcurrentHashMap<String, Marker>();

    public void updateUserPositions(final List<PEUserMapDetails> users) {
        if (users == null) {
            return;
        }

        for (PEUserMapDetails user : users) {
            Marker currentMarker = markers.get(user.userInfo.getEmail());
            if (currentMarker == null) {
                Marker marker = map.addMarker(new MarkerOptions()
                        .position(new LatLng(user.userLocation.getLatitude(), user.userLocation.getLongitude()))
                        .icon(BitmapDescriptorFactory.fromResource(user.isGroup ? R.drawable.marker : R.drawable.red_dot2))
                        .title(user.userInfo.getUIName())
                        .snippet(String.valueOf(users.indexOf(user))));
                markers.put(user.userInfo.getEmail(), marker);
            } else {
                currentMarker.setPosition(new LatLng(user.userLocation.getLatitude(), user.userLocation.getLongitude()));
            }
        }

        //Remove users if they are not provided by the server

        for (String email : markers.keySet()) {
            boolean found = false;
            for (PEUserMapDetails user : users) {
                if (user.userInfo.getEmail().equalsIgnoreCase(email)){
                    found = true;
                    break;
                }
            }

            if(!found) {
                markers.get(email).remove();
                markers.remove(email);
            }
        }

        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            public Bitmap bitmap;

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(final Marker marker) {
                View v = getActivity().getLayoutInflater().inflate(R.layout.group_map_window_adapter, null);
                final ImageView image = (ImageView) v.findViewById(R.id.image);
                TextView email = (TextView) v.findViewById(R.id.userEmail);
                TextView distance = (TextView) v.findViewById(R.id.distance);
                TextView time = (TextView) v.findViewById(R.id.time);

                final PEUserMapDetails user = users.get(Integer.parseInt(marker.getSnippet()));

                if (bitmap == null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                bitmap = BitmapFactory.decodeStream((InputStream) new URL(user.userInfo.getImageUrl()).getContent());
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        marker.showInfoWindow();
                                    }
                                });
                            } catch (IOException e) {
                                new ExceptionHandler(getActivity()).sendError(e, false);
                            }
                        }
                    }).start();
                } else {
                    image.setImageBitmap(bitmap);
                    bitmap = null;
                }

                email.setText(user.userInfo.getEmail());

                int distanceInMeters = (int) getDistance(user.userLocation, UserTracking.instance.getLastPosition());

                String distanceText = distanceInMeters + "m";
                int kmInM = 1000;
                if (distanceInMeters > kmInM) {
                    distanceText = String.format("%.2f", (float) distanceInMeters / kmInM) + "km";
                }

                distance.setText(distanceText);
                int averageSpeed = 20;
                int hInMin = 60;
                time.setText(distanceInMeters * hInMin / averageSpeed / kmInM + "min");
                return v;
            }
        });
    }

    private float getDistance(Coordinates userLocation, Location myLocation) {

        int userTrackIndex = getTrackPointForLocation(userLocation);
        int meTrackIndex = getTrackPointForLocation(new Coordinates(myLocation.getLatitude(), myLocation.getLongitude()));

        if (meTrackIndex > userTrackIndex) {
            int aux = meTrackIndex;
            meTrackIndex = userTrackIndex;
            userTrackIndex = aux;
        }

        float distance = 0;
        for (int i = meTrackIndex + 1; i < userTrackIndex; ++i) {
            final Coordinates coordinatesLast = PETrackCoordinates.track.get(i - 1);
            final Coordinates coordinates = PETrackCoordinates.track.get(i);

            Location locationLast = new Location("") {
                @Override
                public double getLatitude() {
                    return coordinatesLast.getLatitude();
                }

                @Override
                public double getLongitude() {
                    return coordinatesLast.getLongitude();
                }
            };
            Location location = new Location("") {
                @Override
                public double getLatitude() {
                    return coordinates.getLatitude();
                }

                @Override
                public double getLongitude() {
                    return coordinates.getLongitude();
                }
            };
            float[] results = new float[1];
            Location.distanceBetween(
                    locationLast.getLatitude(),
                    locationLast.getLongitude(),
                    location.getLatitude(),
                    location.getLongitude(),
                    results);
            distance += results[0];
        }


        return distance;
    }

    private int getTrackPointForLocation(final Coordinates coordinates) {
        int index = -1;
        float distance = Integer.MAX_VALUE;

        Location location = new Location("") {
            @Override
            public double getLatitude() {
                return coordinates.getLatitude();
            }

            @Override
            public double getLongitude() {
                return coordinates.getLongitude();
            }
        };

        for (int i = 0; i < PETrackCoordinates.track.size(); ++i) {
            final Coordinates trackCoordinates = PETrackCoordinates.track.get(i);

            Location locationTrack = new Location("") {
                @Override
                public double getLatitude() {
                    return trackCoordinates.getLatitude();
                }

                @Override
                public double getLongitude() {
                    return trackCoordinates.getLongitude();
                }
            };

            if (MapTrack.distanceTo(location, locationTrack) < distance) {
                index = i;
                distance = MapTrack.distanceTo(location, locationTrack);
            }
        }

        return index;
    }
}
