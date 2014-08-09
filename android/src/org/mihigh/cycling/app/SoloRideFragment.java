package org.mihigh.cycling.app;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

public class SoloRideFragment extends Fragment {

    private boolean rideStarted = false;

    private Button startButton;
    private LinearLayout stopPauseLayout;
    private Button stopButton;
    private Button pauseButton;

    private GoogleMap mMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.solo_ride, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        startButton = (Button) getView().findViewById(R.id.start);
        stopButton = (Button) getView().findViewById(R.id.stop);
        pauseButton = (Button) getView().findViewById(R.id.pause);
        stopPauseLayout = (LinearLayout) getView().findViewById(R.id.stop_pause);

        if (rideStarted == false) {
            startButton.setVisibility(View.VISIBLE);
            stopPauseLayout.setVisibility(View.GONE);
        } else {
            startButton.setVisibility(View.GONE);
            stopPauseLayout.setVisibility(View.VISIBLE);
        }

        View view = (View) getView().findViewById(R.id.bar1);
        view.getLayoutParams().height = Utils.getSizeFromDP(10, LoginActivity.scale);


        setUpMapIfNeeded();
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                System.out.println("GIGI: " + location.toString());
            }
        });


    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            mMap = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map))
                .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                // The Map is verified. It is now safe to manipulate the map.



            }
        }
    }
}
