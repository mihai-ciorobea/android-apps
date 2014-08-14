package org.mihigh.cycling.app.solo;

import java.util.ArrayList;
import java.util.List;

import android.location.Location;

public class Tracking {

    public static Tracking instance = new Tracking();
    private SoloHomeFragment.RideStatus rideStatus;

    private Tracking() {
    }

    private List<Location> positions = new ArrayList<Location>();


    public String getSpeed() {
        int size = positions.size();
        if (positions.isEmpty() || size < 2) {
            return "0.00";
        }

        float distance = 0;
        float time = 0;
        int previousIndex = size - 2;
        while (distance == 0) {
            distance = positions.get(size - 1).distanceTo(positions.get(previousIndex));
            time = positions.get(size - 1).getElapsedRealtimeNanos() - positions.get(previousIndex).getElapsedRealtimeNanos();
            time = time / (1000 * 1000 * 1000);
            previousIndex--;

            if (previousIndex < 0) {
                return "0.00";
            }
        }

        //from m/s to km/h

        time /= 3600;
        distance /= 1000;

        return String.format("%.2f", (double) distance / time);
    }

    public void addLocation(Location location) {
        if (rideStatus == SoloHomeFragment.RideStatus.STARTED
            || rideStatus == SoloHomeFragment.RideStatus.RESUMED) {
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


    public void setRideStatus(SoloHomeFragment.RideStatus rideStatus) {
        this.rideStatus = rideStatus;
    }
}


