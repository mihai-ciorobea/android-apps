package org.mihigh.cycling.app;

import android.animation.LayoutTransition;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class HomeFragment extends Fragment {

    public static final String USER = "USER_DETAILS";
    private TextView greeting;



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

        greeting = (TextView) getView().findViewById(R.id.greeting);

        Bundle args = getArguments();
        if (args != null) {
            updateHomeView(args.getString(USER));
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

    public void updateHomeView(String userName) {
        greeting.setText(userName);
    }

}
