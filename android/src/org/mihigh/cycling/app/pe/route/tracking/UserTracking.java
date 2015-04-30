package org.mihigh.cycling.app.pe.route.tracking;

import android.location.Location;
import org.mihigh.cycling.app.pe.route.ui.utils.MapTrack;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserTracking {

    public static final UserTracking instance = new UserTracking();

    private List<Location> positions = new ArrayList<Location>();
    public boolean setGroupVisibility = true;
    public boolean setNearbyVisibility = true;

    private UserTracking() {
    }

    public void addLocation(Location location) {
        if (location.getTime() == 0) {
            location.setTime(new Date().getTime());
        }

        if (!positions.isEmpty()) {
            Location lastLocation = positions.get(positions.size() - 1);
            float distanceInMeters = MapTrack.distanceTo(lastLocation, location);
            long timeInSec = (location.getTime() - lastLocation.getTime()) / 1000;
            if (location.getSpeed() == 0) {
                location.setSpeed(distanceInMeters / timeInSec);
            }
        }

        positions.add(location);
    }


    // Metrics
    public String getSpeed() {
        int size = positions.size();
        float speedMps = size != 0 ? positions.get(size - 1).getSpeed() : 0;
        double speedKMps = speedMps * 3.6;

        if (size - 2 >= 0) {
            speedKMps += positions.get(size - 2).getSpeed() * 3.6;
        }

        if (size - 3 >= 0) {
            speedKMps += positions.get(size - 3).getSpeed() * 3.6;
        }

        return String.format("%.2f", speedKMps / 3);
    }


    public String getDistance() {
        if (positions.isEmpty()) {
            return "0.00";
        }

        Location last = positions.get(0);
        int distance = 0;
        for (Location location : positions) {
            distance += MapTrack.distanceTo(last, location);
            last = location;
        }
        return String.format("%.2f", (double) distance / 1000);
    }

    public List<Integer> get5MinActivity() {
        List<Integer> activity = new ArrayList<Integer>();

        Location lastLocation = null;
        float distance = 0;

        for (Location location : positions) {
            if (lastLocation == null) {
                lastLocation = location;
                continue;
            }
            distance += MapTrack.distanceTo(location, lastLocation);

            if (location.getElapsedRealtimeNanos() - lastLocation.getElapsedRealtimeNanos() >= 5L * 60 * 1000 * 1000 * 1000) {
                activity.add((int) distance);

                distance = 0;
                lastLocation = location;
            }
        }

        activity.add((int) distance);
        return activity;
    }


    public String getAverageSpeed() {
        float sum = 0;

        for (Location location : positions) {
            sum += location.getSpeed();
        }

        return String.format("%.2f", (sum / positions.size()) * 3.6);
    }

    public List<Location> getPositions() {
        return positions;
    }

    public Location getLastPosition() {
        if (positions.isEmpty()) {
            return null;
        }
        return positions.get(positions.size() - 1);
    }

    public String getTotalTime() {

        long totalTimeInSec = positions.isEmpty() ? 0 : ((positions.get(positions.size() - 1).getTime() - positions.get(0).getTime()) / 1000);
        int hour = (int) (totalTimeInSec / 3600);
        int minInSec = (int) (totalTimeInSec % 3600);
        int min = minInSec / 60;
        int sec = minInSec % 60;

        return String.format("%02d", hour) + ":"
                + String.format("%02d", min) + ":"
                + String.format("%02d", sec);
    }
}
