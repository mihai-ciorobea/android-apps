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

    private RideStatus rideStatusUpdate = RideStatus.NOT_STARTED;
    private Date previousStarted = new Date();


    private enum RideStatus {
        NOT_STARTED,
        STARTED,
        PAUSED,
        STOPPED, RESUMED;
    }
    private Button startButton;

    private LinearLayout stopPauseLayout;
    private Button stopButton;
    private Button pauseButton;
    private Button resumeButton;
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
                rideStatusUpdate(RideStatus.STARTED);
                stopPauseLayout.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.VISIBLE);
                stopButton.setVisibility(View.VISIBLE);
                startButton.setVisibility(View.GONE);
            }
        });

        stopButton = (Button) getView().findViewById(R.id.stop);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rideStatusUpdate(RideStatus.STOPPED);
            }
        });



        pauseButton = (Button) getView().findViewById(R.id.pause);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rideStatusUpdate(RideStatus.PAUSED);
                resumeButton.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.GONE);
            }
        });


        resumeButton = (Button) getView().findViewById(R.id.resume);
        resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rideStatusUpdate(RideStatus.RESUMED);
                resumeButton.setVisibility(View.GONE);
                pauseButton.setVisibility(View.VISIBLE);
            }
        });

        stopPauseLayout = (LinearLayout) getView().findViewById(R.id.stop_pause);

        View view = (View) getView().findViewById(R.id.bar1);
        view.getLayoutParams().height = Utils.getSizeFromDP(10, LoginActivity.scale);

        setTimerForDisplayingTheTimePassed();
    }

    private void rideStatusUpdate(RideStatus status) {
        rideStatusUpdate = status;
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


    int previousTime = 0;

    //TODO: add logic for stop / pause -- update previous time and previous stared date
    private void pedioticUpdate() {
        if (rideStatusUpdate != RideStatus.STARTED
            && rideStatusUpdate != RideStatus.RESUMED) {
            return;
        }

        Date now = new Date();
        long diffInSeconds = (now.getTime() - previousStarted.getTime()) / 1000;

        long totalTime = previousTime + diffInSeconds;
        time.setText(String.format("%02d", totalTime / 60) + ":" + String.format("%02d", totalTime % 60));

        updateStats();
    }

    private void updateStats() {
        List<Pair<Date, Location>> history = SoloMapFragment.LOCATION_CHANGE_LISTENER.history;


    }

}
