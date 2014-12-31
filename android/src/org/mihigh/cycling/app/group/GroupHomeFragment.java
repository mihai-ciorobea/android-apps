package org.mihigh.cycling.app.group;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import org.mihigh.cycling.app.group.dto.ProgressStatus;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GroupHomeFragment extends Fragment {

    private RideStatus rideStatusUpdate = RideStatus.NOT_STARTED;
    private Date previousStarted;
    private long previousTime = 0;
    private TextView speed;
    private TextView distance;
    private GroupRideFragment groupRideFragment;

    public GroupHomeFragment(GroupRideFragment groupRideFragment) {

        this.groupRideFragment = groupRideFragment;
    }

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
        return inflater.inflate(R.layout.group_home, container, false);
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
                previousStarted = new Date();
                previousTime = 0;
                new Thread(new GroupStartRideRunnable(groupRideFragment.id, GroupHomeFragment.this, ProgressStatus.ACTIVE)).start();
            }
        });

        stopButton = (Button) getView().findViewById(R.id.stop);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rideStatusUpdate(RideStatus.STOPPED);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                    .setTitle("Stop ride")
                    .setMessage("Do you really want to stop")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            rideStatusUpdate(RideStatus.NOT_STARTED);
                            previousStarted = new Date();
                            previousTime = 0;

                            dialog.dismiss();
                            new Thread(new GroupStartRideRunnable(groupRideFragment.id, GroupHomeFragment.this, ProgressStatus.FINISHED)).start();
                            ((LoginActivity) getActivity()).stopGroupRide(groupRideFragment);
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });

        pauseButton = (Button) getView().findViewById(R.id.pause);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rideStatusUpdate(RideStatus.PAUSED);
                previousTime = updateTime();
            }
        });

        resumeButton = (Button) getView().findViewById(R.id.resume);
        resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rideStatusUpdate(RideStatus.RESUMED);
                previousStarted = new Date();
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
        GroupTracking.instance.setRideStatus(status);

        if (status == RideStatus.PAUSED) {
            stopPauseLayout.setVisibility(View.VISIBLE);
            stopButton.setVisibility(View.VISIBLE);
            resumeButton.setVisibility(View.VISIBLE);
            startButton.setVisibility(View.GONE);
            pauseButton.setVisibility(View.GONE);
        }

        if (status == RideStatus.RESUMED) {
            stopPauseLayout.setVisibility(View.VISIBLE);
            stopButton.setVisibility(View.VISIBLE);
            resumeButton.setVisibility(View.GONE);
            startButton.setVisibility(View.GONE);
            pauseButton.setVisibility(View.VISIBLE);
        }

        if (status == RideStatus.STARTED) {
            stopPauseLayout.setVisibility(View.VISIBLE);
            stopButton.setVisibility(View.VISIBLE);
            resumeButton.setVisibility(View.GONE);
            startButton.setVisibility(View.GONE);
            pauseButton.setVisibility(View.VISIBLE);
        }
        if (status == RideStatus.NOT_STARTED) {
            stopPauseLayout.setVisibility(View.GONE);
            startButton.setVisibility(View.VISIBLE);
        }


    }

    private void saveState() {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString("rideStatusUpdate", rideStatusUpdate.toString());
        if (rideStatusUpdate == RideStatus.STARTED
            || rideStatusUpdate == RideStatus.RESUMED) {
            editor.putLong("previousStarted", new Date().getTime());
            editor.putLong("previousTime", updateTime());
        }

        editor.commit();

    }


    private void periodicUpdate() {
        saveState();

        if (rideStatusUpdate != RideStatus.STARTED
            && rideStatusUpdate != RideStatus.RESUMED) {
            return;
        }

        updateBars();
        updateTime();
        updateSpeed();
        updateDistance();
    }

    private void updateDistance() {
        distance.setText(GroupTracking.instance.getDistance());
    }

    private void updateSpeed() {
        speed.setText(GroupTracking.instance.getSpeed());
    }

    private long updateTime() {
        Date now = new Date();
        long diffInSeconds = (now.getTime() - previousStarted.getTime()) / 1000;

        long totalTimeInSec = previousTime + diffInSeconds;
        int hour = (int) (totalTimeInSec / 3600);
        int minInSec = (int) (totalTimeInSec % 3600);
        int min = minInSec / 60;
        int sec = minInSec % 60;

        time.setText(String.format("%02d", hour) + ":"
                     + String.format("%02d", min) + ":"
                     + String.format("%02d", sec));

        return totalTimeInSec;
    }

    private void updateBars() {
        List<Integer> activity = GroupTracking.instance.get5MinActivity();
        int size = activity.size();
        if (size > 9) {
            activity = activity.subList(size - 9, size);
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
