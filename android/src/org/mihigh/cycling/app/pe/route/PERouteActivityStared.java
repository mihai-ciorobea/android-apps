package org.mihigh.cycling.app.pe.route;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.location.LocationListener;
import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.filter.ExceptionHandler;
import org.mihigh.cycling.app.pe.route.help.RequestHelp;
import org.mihigh.cycling.app.pe.route.result.PETrackResult;
import org.mihigh.cycling.app.pe.route.tracking.LocationTracking;
import org.mihigh.cycling.app.pe.route.tracking.UserTracking;
import org.mihigh.cycling.app.pe.route.tracking.collaborative.PECollaborativeLocation;
import org.mihigh.cycling.app.pe.route.tracking.gps.PE_GPS_Service;
import org.mihigh.cycling.app.pe.route.ui.PE_UI_TracingViewPageAdapter;
import org.mihigh.cycling.app.utils.Navigation;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class PERouteActivityStared extends Fragment {

    public static FragmentActivity activity;



    private PECollaborativeLocation collaborativeLocation = new PECollaborativeLocation();
    private PE_GPS_Service gpsService = new PE_GPS_Service();
    private LocationTracking locationTracking = new LocationTracking(collaborativeLocation, gpsService, new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            UserTracking.instance.addLocation(location);
            new Thread(new SendUserPositionRunnable(getActivity(), location)).start();
            new Thread(new GetUsersToShowOnMapRunnable(getActivity(), location, UserTracking.instance.setGroupVisibility,
                    UserTracking.instance.setNearbyVisibility, viewPageAdapter.getMap())).start();
        }
    });

    private TextView time;
    private TextView distance;
    private TextView speed;
    private Timer timer;
    private PE_UI_TracingViewPageAdapter viewPageAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = getActivity();

        return inflater.inflate(R.layout.pe_route_activity_started, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Create wifi p2p setup
        collaborativeLocation.setup(getActivity());

        setupUIComponents();
    }

    private void setupUIComponents() {

        // Setup viewPager. Set default page the map one
        final ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.pe_route_activity_stated_viewPager);
        viewPageAdapter = new PE_UI_TracingViewPageAdapter(getChildFragmentManager(), viewPager, gpsService);
        viewPager.setAdapter(viewPageAdapter);
        viewPager.setCurrentItem(viewPageAdapter.getMapIndex());

        getView().findViewById(R.id.pe_route_activity_stated_send_msg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.changeFragment(getActivity(), R.id.login_fragment_container, new RequestHelp());
            }
        });

        final Button stopButton = (Button) getView().findViewById(R.id.pe_route_activity_stated_stop);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(PERouteActivityStared.this.getActivity());

                alert.setTitle("Stop ride");
                alert.setMessage("Are you sure ?");

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        timer.cancel();
                        locationTracking.stopTracking();

                        Navigation.changeFragment(getActivity(), R.id.login_fragment_container, new PETrackResult());
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();
            }
        });


        time = (TextView) getView().findViewById(R.id.pe_route_activity_stated_time);
        distance = (TextView) getView().findViewById(R.id.pe_route_activity_stated_distance);
        speed = (TextView) getView().findViewById(R.id.pe_route_activity_stated_speed);

        getView().findViewById(R.id.pe_route_activity_stated_zoom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View stopLayout = getView().findViewById(R.id.pe_route_activity_stated_stop_layout);
                stopLayout.setVisibility(stopLayout.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);

                View trackingLayout = getView().findViewById(R.id.pe_route_activity_stated_tracking_layout);
                trackingLayout.setVisibility(trackingLayout.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);

                viewPager.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0,
                        trackingLayout.getVisibility() == View.GONE ? 20f : 17f));

                View mapLayout = getView().findViewById(R.id.pe_route_activity_stated_viewPager_layout);
                mapLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0,
                        trackingLayout.getVisibility() == View.GONE ? 9f : 8f));
            }
        });

        setTimerForDisplayingTheTimePassed();
    }

    private void setTimerForDisplayingTheTimePassed() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

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

    private void periodicUpdate() {
        try {

            updateTime();
            updateSpeed();
            updateDistance();
            viewPageAdapter.updateProgressBars();
        } catch (Exception e) {
            new ExceptionHandler(getActivity()).sendError(e, false);
        }
    }

    private void updateDistance() {
        distance.setText(UserTracking.instance.getDistance());
    }

    private void updateSpeed() {
        speed.setText(UserTracking.instance.getSpeed());
    }

    private void updateTime() {

        long currentDate = new Date().getTime();
        long startDate = UserTracking.instance.getPositions().isEmpty() ? currentDate : UserTracking.instance.getPositions().get(0).getTime();
        long totalTimeInSec = (currentDate - startDate) / 1000;
        int hour = (int) (totalTimeInSec / 3600);
        int minInSec = (int) (totalTimeInSec % 3600);
        int min = minInSec / 60;
        int sec = minInSec % 60;

        time.setText(String.format("%02d", hour) + ":"
                + String.format("%02d", min) + ":"
                + String.format("%02d", sec));
    }

    @Override
    public void onStop() {
//        collaborativeLocation.onStop();
        super.onStop();
    }


    @Override
    public void onResume() {
        super.onResume();
        collaborativeLocation.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
//        collaborativeLocation.onPause();
    }

}

