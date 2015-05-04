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
import android.widget.TextView;
import com.facebook.widget.LoginButton;
import org.mihigh.cycling.app.login.dto.UserInfo;
import org.mihigh.cycling.app.pe.group.dto.PEGroupDetails;

public class HomeFragment extends Fragment {

    public static final String USER = "USER_DETAILS";
    private LoginActivity activity;


    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
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

        activity = (LoginActivity) getActivity();

        return inflater.inflate(R.layout.home, container, false);

    }


    @Override
    public void onStart() {
        super.onStart();

        LayoutTransition l = new LayoutTransition();
        l.enableTransitionType(LayoutTransition.CHANGING);
        ViewGroup viewGroup = (ViewGroup) getView().findViewById(R.id.home_buttons_layout);
        ViewGroup viewLogoutGroup = (ViewGroup) getView().findViewById(R.id.home_logout_buttons_layout);
        viewGroup.setLayoutTransition(l);

        //Set welcome msg
        TextView welcomeMsg = (TextView) getView().findViewById(R.id.home_hello_msg);
        welcomeMsg.setText(UserInfo.restore(activity).getUIName());

//        {
//            Button button = new Button(activity);
//            button.setText("Solo Ride");
//            button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    activity.startSoloRide();
//                }
//            });
//            viewGroup.addView(button);
//        }
//
//        {
//            Button button = new Button(activity);
//            button.setText("Group Ride");
//            button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    activity.startGroupRide();
//                }
//            });
//            viewGroup.addView(button);
//        }


        {
            Button button = new Button(activity);
            button.setText("Prima Evadare");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.startPrimaEvadare();
                }
            });
            viewGroup.addView(button);
        }

        {
            LoginButton loginButton = new LoginButton(activity);
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserInfo.restore(activity).clearStore(activity);
                    PEGroupDetails.setHasGroup(getActivity(), false);

                    FragmentActivity activity = HomeFragment.this.activity;
                    Intent intent = new Intent(activity, LoginActivity.class);
                    activity.startActivity(intent);
                }
            });
            viewLogoutGroup.addView(loginButton);
        }

        Bundle args = getArguments();
        if (args != null) {
            updateHomeView();
        }

        LocationManager manager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
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
