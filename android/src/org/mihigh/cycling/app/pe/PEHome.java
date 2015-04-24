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
                    gotoCreateGroup();
                }
            });
            createButton.setVisibility(!hasGroup ? View.VISIBLE : View.INVISIBLE);
            viewGroup.addView(createButton);
        }

        {
            Button joinButton = new Button(getActivity());
            joinButton.setText("Join Group");
            joinButton.setVisibility(!hasGroup ? View.VISIBLE : View.INVISIBLE);
            joinButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoJoinGroup();
                }
            });
            viewGroup.addView(joinButton);
        }

        {
            Button groupButton = new Button(getActivity());
            groupButton.setText("To Your Group");
            groupButton.setVisibility(hasGroup ? View.VISIBLE : View.INVISIBLE);
            groupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PEGroupDetails.setHasGroup(getActivity(), false);
                    gotoGroupDetails();
                }
            });
            viewGroup.addView(groupButton);
        }

        {
            Button routeButton = new Button(getActivity());
            routeButton.setText("Route");
            routeButton.setVisibility(View.VISIBLE);
            routeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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


    private void gotoCreateGroup() {
        Navigation.changeFragment(getActivity(), R.id.login_fragment_container, new PECreateGroup());
    }

    private void gotoJoinGroup() {
        Navigation.changeFragment(getActivity(), R.id.login_fragment_container, new PEJoinGroup());
    }

    public void gotoGroupDetails() {
        Navigation.changeFragment(getActivity(), R.id.login_fragment_container, new PEGroupHome());
    }

    public void updateHasGroup() {
        Navigation.changeFragment(getActivity(), R.id.login_fragment_container, new PEHome());
    }
}
