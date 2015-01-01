package org.mihigh.cycling.app;

import android.animation.LayoutTransition;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.widget.LoginButton;

public class HomeFragment extends Fragment {

    public static final String USER = "USER_DETAILS";


    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it? The app will not work without it.")
            .setCancelable(false)
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            })
            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                    dialog.cancel();
                }
            });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.home, container, false);

    }


    @Override
    public void onStart() {
        super.onStart();

        LayoutTransition l = new LayoutTransition();
        l.enableTransitionType(LayoutTransition.CHANGING);
        ViewGroup viewGroup = (ViewGroup) getView().findViewById(R.id.home_buttons_layout);
        viewGroup.setLayoutTransition(l);

        {
            Button button = new Button(getActivity());
            button.setText("Solo Ride");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((LoginActivity) getActivity()).startSoloRide();
                }
            });
            viewGroup.addView(button);
        }

        {
            Button button = new Button(getActivity());
            button.setText("Group Ride");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((LoginActivity) getActivity()).startGroupRide();
                }
            });
            viewGroup.addView(button);
        }
        {
            LoginButton loginButton = new LoginButton(getActivity());
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentActivity activity = HomeFragment.this.getActivity();
                    Intent intent = new Intent(activity, LoginActivity.class);
                    activity.startActivity(intent);
                }
            });
            viewGroup.addView(loginButton);

        }

        Bundle args = getArguments();
        if (args != null) {
            updateHomeView();
        }

        LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
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

    public void updateHomeView() {
        //TODO:
    }

}
