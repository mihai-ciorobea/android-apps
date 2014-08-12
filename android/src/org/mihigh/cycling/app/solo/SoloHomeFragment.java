package org.mihigh.cycling.app.solo;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.mihigh.cycling.app.LoginActivity;
import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.Utils;

public class SoloHomeFragment extends Fragment {

    private boolean rideStarted = false;

    private Button startButton;
    private LinearLayout stopPauseLayout;
    private Button stopButton;
    private Button pauseButton;
    private TextView time;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.solo_home, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        time = (TextView) getView().findViewById(R.id.time);

        startButton = (Button) getView().findViewById(R.id.start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rideStarted = true;
                startStopRide();
            }
        });
        stopButton = (Button) getView().findViewById(R.id.stop);
        pauseButton = (Button) getView().findViewById(R.id.pause);
        stopPauseLayout = (LinearLayout) getView().findViewById(R.id.stop_pause);

        startStopRide();

        View view = (View) getView().findViewById(R.id.bar1);
        view.getLayoutParams().height = Utils.getSizeFromDP(10, LoginActivity.scale);

        setTimerForDisplayingTheTimePassed();
    }

    private void startStopRide() {
        if (rideStarted == false) {
            startButton.setVisibility(View.VISIBLE);
            stopPauseLayout.setVisibility(View.GONE);
        } else {
            startButton.setVisibility(View.GONE);
            stopPauseLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setTimerForDisplayingTheTimePassed() {
        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                FragmentActivity activity = getActivity();
                if (activity == null) {
                    this.cancel();
                    return;
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pedioticUpdate();
                    }
                });
            }
        }, 0, 1000);
    }


    Date previousStarted = new Date();
    int previousTime = 0;

    //TODO: add logic for stop / pause -- update previous time and previous stared date
    private void pedioticUpdate() {
        if (!rideStarted) {
            return;
        }

        Date now = new Date();
        long diffInSeconds = (now.getTime() - previousStarted.getTime()) / 1000;

        long totalTime = previousTime + diffInSeconds;
        time.setText(String.format("%02d", totalTime / 60) + ":" + String.format("%02d", totalTime % 60));


        updateStats();
    }

    private void updateStats() {
        List<Pair<Date,Location>> history = SoloMapFragment.LOCATION_CHANGE_LISTENER.history;


    }

}
