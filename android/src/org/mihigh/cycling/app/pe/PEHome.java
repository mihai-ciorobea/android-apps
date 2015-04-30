package org.mihigh.cycling.app.pe;

import android.animation.LayoutTransition;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.pe.group.create.PECreateGroup;
import org.mihigh.cycling.app.pe.group.details.PEGroupHome;
import org.mihigh.cycling.app.pe.group.dto.PECheckGroupForUserRunnable;
import org.mihigh.cycling.app.pe.group.dto.PEGroupDetails;
import org.mihigh.cycling.app.pe.group.join.PEJoinGroup;
import org.mihigh.cycling.app.pe.route.PERouteHome;
import org.mihigh.cycling.app.utils.Navigation;

public class PEHome extends Fragment {

    private boolean hasGroup = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.pe_home, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        checkHasGroup();

        LayoutTransition l = new LayoutTransition();
        l.enableTransitionType(LayoutTransition.CHANGING);
        ViewGroup viewGroup = (ViewGroup) getView().findViewById(R.id.pe_home_buttons_container);
        viewGroup.setLayoutTransition(l);

        {
            Button createButton = new Button(getActivity());
            createButton.setText("Create Group");
            createButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Navigation.changeFragment(getActivity(), R.id.login_fragment_container, new PECreateGroup());
                }
            });
            createButton.setVisibility(!hasGroup ? View.VISIBLE : View.GONE);
            viewGroup.addView(createButton);
        }

        {
            Button joinButton = new Button(getActivity());
            joinButton.setText("Join Group");
            joinButton.setVisibility(!hasGroup ? View.VISIBLE : View.GONE);
            joinButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Navigation.changeFragment(getActivity(), R.id.login_fragment_container, new PEJoinGroup());
                }
            });
            viewGroup.addView(joinButton);
        }

        {
            Button groupButton = new Button(getActivity());
            groupButton.setText("To Your Group");
            groupButton.setVisibility(hasGroup ? View.VISIBLE : View.GONE);
            groupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Navigation.changeFragment(getActivity(), R.id.login_fragment_container, new PEGroupHome());
                }
            });
            viewGroup.addView(groupButton);
        }

        {
            Button space = new Button(getActivity());
            space.setVisibility(View.INVISIBLE);
            viewGroup.addView(space);
        }

        {
            Button routeButton = new Button(getActivity());
            routeButton.setText("Route");
            routeButton.setVisibility(View.VISIBLE);
            routeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Navigation.changeFragment(getActivity(), R.id.login_fragment_container, new PERouteHome());
                }
            });
            viewGroup.addView(routeButton);
        }

    }

    private void checkHasGroup() {
        hasGroup = PEGroupDetails.getHasGroup(getActivity());
        if (!hasGroup) {
            //go on server and check if user has a group
            new Thread(new PECheckGroupForUserRunnable(PEHome.this)).start();
        }
    }

    public void updateHasGroup() {
        Navigation.changeFragment(getActivity(), R.id.login_fragment_container, new PEHome());
    }
}
