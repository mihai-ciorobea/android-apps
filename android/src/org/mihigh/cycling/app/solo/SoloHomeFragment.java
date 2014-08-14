package org.mihigh.cycling.app.solo;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
    private Date previousStarted;
    private long previousTime = 0;
    private TextView speed;
    private TextView distance;

    public enum RideStatus {
        NOT_STARTED,
        STARTED,
        PAUSED,
        STOPPED,
        RESUMED
    }

    private Button startButton;

    private LinearLayout stopPauseLayout;
    private Button stopButton;
    private Button pauseButton;
    private Button resumeButton;
    private TextView time;

    private LinearLayout barLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.solo_home, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        time = (TextView) getView().findViewById(R.id.time);
        speed = (TextView) getView().findViewById(R.id.speed);
        distance = (TextView) getView().findViewById(R.id.distance);
        barLayout = (LinearLayout) getView().findViewById(R.id.performance_bars);

        startButton = (Button) getView().findViewById(R.id.start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rideStatusUpdate(RideStatus.STARTED);
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
            }
        });

        resumeButton = (Button) getView().findViewById(R.id.resume);
        resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rideStatusUpdate(RideStatus.RESUMED);

            }
        });

        stopPauseLayout = (LinearLayout) getView().findViewById(R.id.stop_pause);

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        rideStatusUpdate = RideStatus.valueOf(sharedPref.getString("rideStatusUpdate", RideStatus.NOT_STARTED.toString()));
        previousTime = sharedPref.getLong("previousTime", 0L);
        previousStarted = new Date(sharedPref.getLong("previousStarted", 0L));
        setTimerForDisplayingTheTimePassed();
        rideStatusUpdate(rideStatusUpdate);
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
                        periodicUpdate();
                    }
                });
            }
        }, 0, 1000);
    }


    private void rideStatusUpdate(RideStatus status) {
        rideStatusUpdate = status;
        Tracking.instance.setRideStatus(status);

        if (status == RideStatus.PAUSED) {
            previousTime = updateTime();
            saveState();
            resumeButton.setVisibility(View.VISIBLE);
            pauseButton.setVisibility(View.GONE);
        }

        if (status == RideStatus.RESUMED) {
            previousStarted = new Date();
            saveState();
            resumeButton.setVisibility(View.GONE);
            pauseButton.setVisibility(View.VISIBLE);
        }

        if (status == RideStatus.STARTED) {
            saveState();
            stopPauseLayout.setVisibility(View.VISIBLE);
            pauseButton.setVisibility(View.VISIBLE);
            stopButton.setVisibility(View.VISIBLE);
            startButton.setVisibility(View.GONE);
        }
    }

    private void saveState() {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString("rideStatusUpdate", rideStatusUpdate.toString());
        editor.putLong("previousStarted", new Date().getTime());
        editor.putLong("previousTime", updateTime());

        editor.commit();

    }


    private void periodicUpdate() {
        if (rideStatusUpdate != RideStatus.STARTED
            && rideStatusUpdate != RideStatus.RESUMED) {
            return;
        }

        updateBars();
        updateTime();
        updateSpeed();
        updateDistance();
        saveState();

    }

    private void updateDistance() {
        distance.setText(Tracking.instance.getDistance());
    }

    private void updateSpeed() {
        speed.setText(Tracking.instance.getSpeed());
    }

    private long updateTime() {
        Date now = new Date();
        long diffInSeconds = (now.getTime() - previousStarted.getTime()) / 1000;

        long totalTime = previousTime + diffInSeconds;
        time.setText(String.format("%02d", totalTime / 60) + ":" + String.format("%02d", totalTime % 60));

        return totalTime;
    }

    private void updateBars() {
        List<Integer> activity = Tracking.instance.get5MinActivity();
        int size = activity.size();
        if (size > 9) {
            activity.subList(size - 9, size);
        }

        int maxVal = Collections.max(activity);
        if (maxVal != 0) {
            for (int index = 0; index < activity.size(); ++index) {
                int distance = activity.get(index);

                int barSize = distance * 100 / maxVal;

                barLayout.getChildAt(index).getLayoutParams().height =
                    Math.max(Utils.getSizeFromDP(barSize, LoginActivity.scale), 10);
            }

            barLayout.requestLayout();
        }
    }
}
