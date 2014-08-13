package org.mihigh.cycling.app.solo;

import java.util.ArrayList;
import java.util.List;

import android.location.Location;

public class Tracking {

    public static Tracking instance = new Tracking();

    private Tracking() {
    }

    private List<Location> positions = new ArrayList<Location>();

    public void addLocation(Location location) {
        positions.add(location);
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


}


