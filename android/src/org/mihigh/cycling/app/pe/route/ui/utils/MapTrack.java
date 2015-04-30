package org.mihigh.cycling.app.pe.route.ui.utils;

import android.location.Location;
import android.view.ViewTreeObserver;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.*;
import org.mihigh.cycling.app.group.dto.Coordinates;
import org.mihigh.cycling.app.pe.route.PETrackCoordinates;

public class MapTrack {

    public static void setupTrack(final MapView mapView, final GoogleMap map) {
        // Get track location
        PolylineOptions polylineOptions = new PolylineOptions();
        for (Coordinates mark : PETrackCoordinates.track) {
            LatLng point = new LatLng(mark.getLatitude(), mark.getLongitude());
            polylineOptions.add(point);
        }
        map.addPolyline(polylineOptions);
    }

    public static void setupCamera(final MapView mapView, final GoogleMap map) {

        // Get track bounds
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Coordinates mark : PETrackCoordinates.track) {
            LatLng point = new LatLng(mark.getLatitude(), mark.getLongitude());
            builder.include(point);
        }

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

    public static void addStartMarker(GoogleMap map) {
        // Add start marker
        map.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .position(new LatLng(PETrackCoordinates.track.get(0).getLatitude(), PETrackCoordinates.track.get(0).getLongitude())));
    }

    public static float distanceTo(Location start, Location stop ){
        float[] results = new float[1];
        Location.distanceBetween(
                start.getLatitude(),
                start.getLongitude(),
                stop.getLatitude(),
                stop.getLongitude(),
                results);
        return results[0];
    }
}
