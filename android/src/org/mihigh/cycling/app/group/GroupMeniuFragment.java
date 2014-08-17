package org.mihigh.cycling.app.group;

import android.animation.LayoutTransition;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.mihigh.cycling.app.LoginActivity;
import org.mihigh.cycling.app.R;

public class GroupMeniuFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.group_meniu, container, false);
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
            button.setText("Search for a ride");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((LoginActivity) getActivity()).searchForRide();
                }
            });
            viewGroup.addView(button);
        }

        {
            Button button = new Button(getActivity());
            button.setText("Joined Rides");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((LoginActivity) getActivity()).joinedRides();
                }
            });
            viewGroup.addView(button);
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
