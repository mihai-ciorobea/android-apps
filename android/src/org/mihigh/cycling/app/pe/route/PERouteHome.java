package org.mihigh.cycling.app.pe.route;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.*;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.group.dto.Coordinates;
import org.mihigh.cycling.app.utils.LoadingUtils;
import org.mihigh.cycling.app.utils.Navigation;

public class PERouteHome extends Fragment {

    boolean useCollaborativeTracking = true;
    boolean isGPSEnabled = true;
    boolean is3GEnabled = false;
    boolean isBlueToothEnabled = false;


    MapView mapView;
    GoogleMap map;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View layout = inflater.inflate(R.layout.pe_route_home, container, false);

        try {
            MapsInitializer.initialize(getActivity());
        } catch (Exception e) {
            Log.e("Address Map", "Could not initialize google play", e);
        }
        switch (GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity())) {
            case ConnectionResult.SUCCESS:
                mapView = (MapView) layout.findViewById(R.id.pe_route_home_map);
                mapView.onCreate(savedInstanceState);
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

        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();

        setupTrack();
        setupStartButton();
        setupCollaborativeSwitch();


    }

    private void setupStartButton() {
        getView().findViewById(R.id.pe_route_home_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkGPS();
            }

            private void checkYourPosition() {
                LoadingUtils.makeToast(getActivity(), "BLA");
                Navigation.changeFragment(getActivity(), R.id.login_fragment_container, new PEActivityStared());
            }

            private void checkBluetooth() {
                final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    isBlueToothEnabled = false;
                    checkYourPosition();
                    return;
                }

                if (!mBluetoothAdapter.isEnabled()) {

                    AlertDialog.Builder alert = new AlertDialog.Builder(PERouteHome.this.getActivity());
                    alert.setTitle("Collaborative Reporting");
                    alert.setMessage("Reuse your neighbour location via bluetooth. This option is optional.");
                    alert.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
                            startActivity(discoverableIntent);

                            isBlueToothEnabled = true;
                            checkYourPosition();
                        }
                    });
                    alert.setNegativeButton("Continue anyway", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            isBlueToothEnabled = false;
                            checkYourPosition();
                        }
                    });
                    alert.show();
                } else {

                    Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
                    startActivity(discoverableIntent);

                    mBluetoothAdapter.setName("NewDeviceName");
                    isBlueToothEnabled = true;
                    checkYourPosition();
                }
            }

            private void check3G() {
                ConnectivityManager service = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

                if (service == null) {
                    is3GEnabled = false;
                    checkBluetooth();
                    return;
                }

                boolean enabled = service.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting()
                        || service.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();

                if (!enabled) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(PERouteHome.this.getActivity());
                    alert.setTitle("Reporting");
                    alert.setMessage("Let us know were you are. Enable you 3G/WIFI service. This option is optional.");
                    alert.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    });
                    alert.setNegativeButton("Continue anyway", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            is3GEnabled = false;
                            checkBluetooth();
                        }
                    });
                    alert.show();
                } else {
                    is3GEnabled = true;
                    checkBluetooth();
                }
            }

            private void checkGPS() {
                LocationManager service = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

                if (service == null) {
                    LoadingUtils.makeToast(getActivity(), "Your device does not support GPS");
                    return;
                }

                boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

                if (!enabled) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(PERouteHome.this.getActivity());
                    alert.setTitle("Tracking");
                    alert.setMessage("Please enable your GPS service");
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    });
                    alert.show();
                } else {
                    isGPSEnabled = true;
                    if (useCollaborativeTracking) {
                        check3G();
                    } else {
                        checkYourPosition();
                    }
                }
            }
        });
    }

    private void setupCollaborativeSwitch() {
        final ToggleButton collaborativeToggle = (ToggleButton) getView().findViewById(R.id.pe_route_home_collaborative_switch);
        collaborativeToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                useCollaborativeTracking = collaborativeToggle.isChecked();

                if (!collaborativeToggle.isChecked()) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(PERouteHome.this.getActivity());
                    alert.setTitle("Collaborative Tracking");
                    alert.setMessage("This option is collecting the position of your neighbours so that you don't need to use your GPS." +
                            "Leaving this feature on, may save more of you battery. \n\nAre you sure you want to disable it?");
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            collaborativeToggle.setChecked(false);
                            useCollaborativeTracking = false;
                        }
                    });
                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            collaborativeToggle.setChecked(true);
                            useCollaborativeTracking = true;
                        }
                    });
                    alert.show();
                }
            }
        });
    }

    private void setupTrack() {
        // Add track on map
        PolylineOptions polylineOptions = new PolylineOptions();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Coordinates mark : PETrackCoordinates.track) {
            LatLng point = new LatLng(mark.getLatitude(), mark.getLongitude());
            polylineOptions.add(point);
            builder.include(point);
        }
        map.addPolyline(polylineOptions);


        // Add start marker
        map.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .position(new LatLng(PETrackCoordinates.track.get(0).getLatitude(), PETrackCoordinates.track.get(0).getLongitude())));

        // Setup zoom & bounds
        LatLngBounds bounds = builder.build();
        int padding = 20; // offset from edges of the map in pixels
        final CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);


        mapView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        map.animateCamera(cameraUpdate);
                    }
                });
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


    public static abstract class LocationResult {
        public abstract void gotLocation(Location location);
    }
}



