package org.mihigh.cycling.app.pe.route.tracking;

import android.location.Location;
import android.os.Handler;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.model.LatLng;
import org.mihigh.cycling.app.pe.route.PERouteActivityStared;
import org.mihigh.cycling.app.pe.route.tracking.collaborative.PECollaborativeLocation;
import org.mihigh.cycling.app.pe.route.tracking.gps.PE_GPS_Service;
import org.mihigh.cycling.app.utils.LoadingUtils;

import java.util.List;

public class LocationTracking implements LocationListener {

    public static final int GPS_UPDATE_TIME = 1000 * 7;

    private final PECollaborativeLocation collaborativeLocation;
    private final PE_GPS_Service gpsService;
    private LocationListener notifyUI;
    private boolean stillRunning = true;

    public LocationTracking(PECollaborativeLocation collaborativeLocation, PE_GPS_Service gpsService, LocationListener notifyUI) {
        this.collaborativeLocation = collaborativeLocation;
        this.gpsService = gpsService;
        this.notifyUI = notifyUI;

        gpsService.setUILocationChangedListener(this);
    }

    public void startTracking() {
        if (!stillRunning) {
            return;
        }

        collaborativeLocation.discoverServices();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                collaborativeLocation.onPause();
                List<LatLng> neighbours = collaborativeLocation.getNeighbours();

                if (neighbours.isEmpty()) {
                    // No neighbours, start GPS
                    gpsService.onResume();
                } else {
                    LoadingUtils.makeToast(PERouteActivityStared.activity, "COLLABORATIVE");

                    // Send data to gpsService
                    double lat = 0;
                    double lng = 0;

                    for (LatLng neighbour : neighbours) {
                        lat += neighbour.latitude;
                        lng += neighbour.longitude;
                    }

                    lat /= neighbours.size();
                    lng /= neighbours.size();

                    final LatLng latLng = new LatLng(lat, lng);
                    gpsService.receivedCollaborativeLocation(latLng);
                    notifyUI.onLocationChanged(new Location("COLLABORATIVE") {
                        @Override
                        public double getLatitude() {
                            return latLng.latitude;
                        }

                        @Override
                        public double getLongitude() {
                            return latLng.longitude;
                        }
                    });

                    // start scanning again
                    startTracking();
                }
            }
        }, GPS_UPDATE_TIME);
    }

    public void stopTracking() {

        stillRunning = false;

        collaborativeLocation.onPause();
        collaborativeLocation.onStop();

        gpsService.stop();
    }

    @Override
    public void onLocationChanged(Location location) {
        //via GPS

        LoadingUtils.makeToast(PERouteActivityStared.activity, "GPS");
        collaborativeLocation.updateLocation(location);
        startTracking();
        location.setProvider("GPS");
        notifyUI.onLocationChanged(location);
    }

    public void gpsGettingSlow() {
        collaborativeLocation.onPause();
    }
}
