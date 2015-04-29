package org.mihigh.cycling.app.pe.route.tracking;

import android.location.Location;
import android.os.Handler;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.model.LatLng;
import org.mihigh.cycling.app.pe.route.tracking.collaborative.PECollaborativeLocation;
import org.mihigh.cycling.app.pe.route.tracking.gps.PE_GPS_Service;

import java.util.List;

public class LocationTracking implements LocationListener {

    public static final int GPS_UPDATE_TIME = 1000 * 7;

    private final PECollaborativeLocation collaborativeLocation;
    private final PE_GPS_Service gpsService;

    public LocationTracking(PECollaborativeLocation collaborativeLocation, PE_GPS_Service gpsService) {
        this.collaborativeLocation = collaborativeLocation;
        this.gpsService = gpsService;

        gpsService.setUILocationChangedListener(this);
    }

    public void startTracking() {
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

                    // Send data to gpsService
                    double lat = 0;
                    double lng = 0;

                    for (LatLng neighbour : neighbours) {
                        lat += neighbour.latitude;
                        lng += neighbour.longitude;
                    }

                    lat /= neighbours.size();
                    lng /= neighbours.size();

                    gpsService.receivedCollaborativeLocation(new LatLng(lat, lng));

                    // start scanning again
                    startTracking();
                }
            }
        }, GPS_UPDATE_TIME);
    }

    @Override
    public void onLocationChanged(Location location) {
        //receiveLocationViaGPSService
        collaborativeLocation.updateLocation(location);
        startTracking();
    }

    public void gpsGettingSlow() {
        collaborativeLocation.onPause();
    }
}
