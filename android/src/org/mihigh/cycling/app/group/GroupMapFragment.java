package org.mihigh.cycling.app.group;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import org.mihigh.cycling.app.LoginActivity;
import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.group.dto.Coordinates;
import org.mihigh.cycling.app.group.dto.UserMapDetails;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class GroupMapFragment extends Fragment {

    MapView mapView;
    GoogleMap map;
    private long id;
    private Bitmap bitmap;

    private ViewPager viewPager;
    public GroupMapFragment(long id, ViewPager mPager) {
        this.id = id;
        this.viewPager = mPager;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.group_map, container, false);

        Button backButton = (Button) v.findViewById(R.id.mapback);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
            }
        });

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
                            new Thread(new GroupMyPositionRunnable(id, location.getLatitude(), location.getLongitude(), GroupMapFragment.this)).start();
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

    UserMapDetails me;
    HashMap<String, Marker> markers = new HashMap<String, Marker>();


    public void updateAllUsers(final List<UserMapDetails> usersInfo) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (usersInfo == null) {
                    return;
                }
                for (UserMapDetails userInfo : usersInfo) {
                    if (userInfo.user.getEmail().equalsIgnoreCase(LoginActivity.userInfo.getEmail())) {
                        me = userInfo;
                        break;
                    }
                }

                for (UserMapDetails userInfo : usersInfo) {
                    if (userInfo.coordinates.isEmpty()) {
                        continue;
                    }
                    String email = userInfo.user.getEmail();
                    if (email.equalsIgnoreCase(me.user.getEmail())) {
                        continue;
                    }

                    Marker currentMarker = markers.get(email);
                    List<Coordinates> coordinates = userInfo.coordinates;
                    if (currentMarker == null) {
                        Marker marker = map.addMarker(new MarkerOptions()
                                                          .position(new LatLng(coordinates.get(coordinates.size() - 1).getLatitude(),
                                                                               coordinates.get(coordinates.size() - 1).getLongitude()))
                                                          .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                                                          .title(email)
                                                          .snippet(String.valueOf(usersInfo.indexOf(userInfo))));
                        markers.put(email, marker);
                    } else {
                        currentMarker.setPosition(new LatLng(coordinates.get(coordinates.size() - 1).getLatitude(),
                                                             coordinates.get(coordinates.size() - 1).getLongitude()));
                    }

                    //TODO: keep window open even on refresh of data
                    map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
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

                            final UserMapDetails userInfo = usersInfo.get(Integer.parseInt(marker.getSnippet()));

//                            LoadImageFromWebOperations(image, userInfo.user.getImageUrl(), marker, getActivity());

                            if (bitmap == null) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            bitmap = BitmapFactory.decodeStream((InputStream) new URL(userInfo.user.getImageUrl()).getContent());
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    marker.showInfoWindow();
                                                }
                                            });
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                            } else {
                                image.setImageBitmap(bitmap);
                                bitmap = null;
                            }

                            email.setText(userInfo.user.getEmail());

                            distance.setText(getDistance(userInfo.coordinates, me.coordinates) + "m");
                            time.setText("");
                            return v;
                        }
                    });
                }
            }
        });
    }

    private int getDistance(List<Coordinates> coordinates, List<Coordinates> myCoordinates) {
        int cSize = coordinates.size();
        int mycSize = myCoordinates.size();

        Coordinates lastPos = coordinates.get(cSize - 1);
        Location lastLocation = new Location("");
        lastLocation.setLatitude(lastPos.getLatitude());
        lastLocation.setLongitude(lastPos.getLongitude());

        Coordinates myLastPos = myCoordinates.get(mycSize - 1);
        Location myLastLocation = new Location("");
        myLastLocation.setLatitude(myLastPos.getLatitude());
        myLastLocation.setLongitude(myLastPos.getLongitude());

        double minC = Integer.MAX_VALUE;
        double minMyC = Integer.MAX_VALUE;
        int pozC = 0;
        int myPozC = 0;

        for (Coordinates c : coordinates) {
            Location location = new Location("");
            location.setLatitude(c.getLatitude());
            location.setLongitude(c.getLongitude());

            minC = Math.min(minC, location.distanceTo(myLastLocation));
            if (minC == location.distanceTo(myLastLocation)) {
                pozC = coordinates.indexOf(c);
            }
        }

        for (Coordinates c : myCoordinates) {
            Location location = new Location("");
            location.setLatitude(c.getLatitude());
            location.setLongitude(c.getLongitude());

            minMyC = Math.min(minMyC, location.distanceTo(lastLocation));
            if (minMyC == location.distanceTo(lastLocation)) {
                myPozC = coordinates.indexOf(c);
            }
        }

        List<Coordinates> coords;
        int poz;
        if (minC < minMyC) {
            coords = coordinates;
            poz = pozC;
        } else {
            coords = myCoordinates;
            poz = myPozC;
        }

        if (poz < 0) {
            return 0;
        }
        int distance = 0;
        lastLocation = null;
        for (int i = poz; i < coords.size(); ++i) {

            Coordinates c = coords.get(i);
            Location location = new Location("");
            location.setLatitude(c.getLatitude());
            location.setLongitude(c.getLongitude());

            if (lastLocation == null) {
                lastLocation = location;
                continue;
            }

            distance += lastLocation.distanceTo(location);
            lastLocation = location;
        }

        return distance;
    }

    public void removeListner() {
        map.setOnMyLocationChangeListener(null);
    }

    public static void LoadImageFromWebOperations(final ImageView image, final String url, final Marker marker, final FragmentActivity activity) {

    }

}
