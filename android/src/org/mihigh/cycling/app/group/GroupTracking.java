package org.mihigh.cycling.app.group;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class GroupTracking {

    public static GroupTracking instance = new GroupTracking();
    private GroupHomeFragment.RideStatus rideStatus;

    private GroupTracking() {
    }

    private List<Location> positions = new ArrayList<Location>();

    public List<Location> getPositions() {
        return positions;
    }

    public void setPositions(List<Location> positions) {
        this.positions = positions;
    }

    public String getSpeed() {
        int size = positions.size();
        float speedMps = size != 0 ? positions.get(size - 1).getSpeed() : 0;
        double speedKMps = speedMps * 3.6;

        return String.format("%.2f", speedKMps);
    }

    public void addLocation(Location location) {
        if (rideStatus == GroupHomeFragment.RideStatus.STARTED
            || rideStatus == GroupHomeFragment.RideStatus.RESUMED) {
            positions.add(location);
        }
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
            distance += location.distanceTo(lastLocation);

            if (location.getElapsedRealtimeNanos() - lastLocation.getElapsedRealtimeNanos() >= 5L * 60 * 1000 * 1000 * 1000) {
                activity.add((int) distance);

                distance = 0;
                lastLocation = location;
            }
        }

        activity.add((int) distance);
        return activity;
    }


    public String getDistance() {
        if (positions.isEmpty()) {
            return "0.0";
        }

        Location last = positions.get(0);
        int distance = 0;
        for (Location location : positions) {
            distance += location.distanceTo(last);
            last = location;
        }
        return String.format("%.2f", (double) distance / 1000);
    }


    public void setRideStatus(GroupHomeFragment.RideStatus rideStatus) {
        this.rideStatus = rideStatus;
    }
}


