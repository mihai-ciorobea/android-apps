package org.mihigh.cycling.app.pe.route.tracking.gps;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.model.LatLng;
import org.mihigh.cycling.app.pe.route.tracking.LocationTracking;
import org.mihigh.cycling.app.utils.LoadingUtils;

import java.util.concurrent.atomic.AtomicInteger;

public class PE_GPS_Service {

    // These settings are the same as the settings for the map. They will in fact give you updates at
    // the maximal rates currently possible.
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)              // 5 seconds
            .setFastestInterval(2000)       // 2 seconds
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    //    private final FragmentActivity activity;
    private LocationClient locationClient;
    private LocationSource.OnLocationChangedListener onLocationChangedListener;
    private LocationTracking locationTracking;

    public static AtomicInteger gpsScanId = new AtomicInteger(0);

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            gpsScanId.incrementAndGet();

            // notify GoogleMap
            onLocationChangedListener.onLocationChanged(location);

            // location received via GPS
            // notify collaborative service to share it
            locationTracking.onLocationChanged(location);
            stop();
        }
    };

    public LocationSource locationService = new LocationSource() {
        @Override
        public void activate(LocationSource.OnLocationChangedListener onLocationChangedListener) {
            PE_GPS_Service.this.onLocationChangedListener = onLocationChangedListener;
        }

        @Override
        public void deactivate() {

        }
    };

    public PE_GPS_Service() {
    }


    public void start(final FragmentActivity activity) {
        if (locationClient == null) {
            locationClient = new LocationClient(
                    activity.getApplicationContext(),
                    // ConnectionCallbacks
                    new GooglePlayServicesClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {
                            locationClient.requestLocationUpdates(REQUEST, locationListener);
                        }

                        @Override
                        public void onDisconnected() {
                            LoadingUtils.makeToast(activity, "disconnected");
                        }
                    },
                    // OnConnectionFailedListener
                    new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {
                            LoadingUtils.makeToast(activity, "onConnectionFailed");
                        }
                    }
            );
        }
        locationClient.connect();
    }


    public void stop() {
//        locationClient.removeLocationUpdates(locationListener);
        this.onPause();
    }

    public void receivedCollaborativeLocation(final LatLng latLng) {
        onLocationChangedListener.onLocationChanged(new Location("") {
            @Override
            public double getLatitude() {
                return latLng.latitude;
            }

            @Override
            public double getLongitude() {
                return latLng.longitude;
            }
        });
    }



    public void onResume() {
        final int startNumber = gpsScanId.get();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int timeoutNumber = gpsScanId.get();

                if (startNumber == timeoutNumber) {
                    locationTracking.gpsGettingSlow();
                }
            }
        }, LocationTracking.GPS_UPDATE_TIME);
        locationClient.connect();
    }

    public void onPause() {
        locationClient.disconnect();
    }

    public void setUILocationChangedListener(LocationTracking locationTracking) {
        this.locationTracking = locationTracking;
    }
}
