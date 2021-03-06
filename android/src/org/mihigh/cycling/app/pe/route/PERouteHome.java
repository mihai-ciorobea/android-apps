package org.mihigh.cycling.app.pe.route;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.filter.ExceptionHandler;
import org.mihigh.cycling.app.pe.route.ui.utils.MapTrack;
import org.mihigh.cycling.app.utils.LoadingUtils;
import org.mihigh.cycling.app.utils.Navigation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class PERouteHome extends Fragment {

    boolean useCollaborativeTracking = true;
    boolean isGPSEnabled = true;
    boolean isWifiEnabled = false;
    boolean is3GEnabled = false;


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

        MapTrack.setupTrack(mapView, map);
        MapTrack.addStartMarker(map);
        MapTrack.setupCamera(mapView, map);

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
                //TODO show working if not nearby start possition
                Navigation.changeFragment(getActivity(), R.id.login_fragment_container, new PERouteActivityStared());
            }


            private void check3G() {
                ConnectivityManager service = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                if (service == null) {
                    is3GEnabled = false;
                    checkYourPosition();
                    return;
                }
                boolean enabled = service.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();

                if (!enabled) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(PERouteHome.this.getActivity());
                    alert.setTitle("Reporting");
                    alert.setMessage("Let us know were you are. \nEnable you 3G service.");
                    alert.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            try {
                                final ConnectivityManager conman = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                                final Class conmanClass = Class.forName(conman.getClass().getName());
                                final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
                                iConnectivityManagerField.setAccessible(true);
                                final Object iConnectivityManager = iConnectivityManagerField.get(conman);
                                final Class iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
                                final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
                                setMobileDataEnabledMethod.setAccessible(true);

                                setMobileDataEnabledMethod.invoke(iConnectivityManager, true);
                                is3GEnabled = true;
                                checkYourPosition();
                            } catch (Exception e) {
                                new ExceptionHandler(getActivity()).sendError(e, false);

                                is3GEnabled = false;
                                checkYourPosition();
                            }
                        }
                    });
                    alert.setNegativeButton("Continue anyway", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            is3GEnabled = false;
                            checkYourPosition();
                        }
                    });
                    alert.show();
                } else {
                    is3GEnabled = true;
                    checkYourPosition();
                }

            }

            private void checkWifi() {
                ConnectivityManager service = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

                if (service == null) {
                    isWifiEnabled = false;
                    check3G();
                    return;
                }

                boolean enabled = service.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();

                if (!enabled) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(PERouteHome.this.getActivity());
                    alert.setTitle("Collaborative tracking");
                    alert.setMessage("We use WIFI P2P to save battery instead of GPS. \n Enable WIFI service?");
                    alert.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            WifiManager wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
                            wifiManager.setWifiEnabled(true);
                            isWifiEnabled = false;
                            check3G();
                        }
                    });
                    alert.setNegativeButton("Continue anyway", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            isWifiEnabled = false;
                            check3G();
                        }
                    });
                    alert.show();
                } else {
                    isWifiEnabled = true;
                    check3G();
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
                        checkWifi();
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



