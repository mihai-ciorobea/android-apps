package org.mihigh.cycling.app.group;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.mihigh.cycling.app.LoginActivity;
import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.group.dto.ProgressStatus;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class GroupHomeFragment extends Fragment {

    private RideStatus rideStatusUpdate = RideStatus.NOT_STARTED;
    private Date previousStarted;
    private long previousTime = 0;
    private TextView speed;
    private TextView distance;
    private long rideId;
    private ViewPager mPager;
    private ScreenSlidePagerAdapter mPagerAdapter;


    public GroupHomeFragment(long rideId) {

        this.rideId = rideId;
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

        startButton = (Button) getView().findViewById(R.id.start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rideStatusUpdate(RideStatus.STARTED);
                previousStarted = new Date();
                previousTime = 0;
                new Thread(new GroupStartRideRunnable(rideId, GroupHomeFragment.this, ProgressStatus.ACTIVE)).start();
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
                            new Thread(new GroupStartRideRunnable(rideId, GroupHomeFragment.this, ProgressStatus.FINISHED)).start();
                            ((LoginActivity) getActivity()).stopGroupRide();
                            mPagerAdapter.getMap().removeListner();
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



        //
        mPager = (ViewPager) getActivity().findViewById(R.id.group_pager);

        if (mPagerAdapter == null) {
            mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());

        }
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(1);
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



    public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {


        private GroupProgressBars groupProgressBars;
        private GroupMapFragment groupMapFragment;

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
            groupMapFragment = new GroupMapFragment(rideId, mPager);
            groupProgressBars = new GroupProgressBars();
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return groupProgressBars;
            }

            return groupMapFragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        public GroupMapFragment getMap() {
            return groupMapFragment;
        }
    }
}
