package org.mihigh.cycling.app.pe;

import android.animation.LayoutTransition;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.pe.groups.create.PECreateGroup;

public class PEHome extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.pe_home, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        LayoutTransition l = new LayoutTransition();
        l.enableTransitionType(LayoutTransition.CHANGING);
        ViewGroup viewGroup = (ViewGroup) getView().findViewById(R.id.pe_home_buttons_container);
        viewGroup.setLayoutTransition(l);

        {
            Button button = new Button(getActivity());
            button.setText("Create Group");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoCreateGroup();
                }
            });
            viewGroup.addView(button);
        }

        {
            Button button = new Button(getActivity());
            button.setText("Join Group");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            viewGroup.addView(button);
        }

        {
            Button button = new Button(getActivity());
            button.setText("To Your Group");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            viewGroup.addView(button);
        }

        {
            Button button = new Button(getActivity());
            button.setText("Route");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            viewGroup.addView(button);
        }

    }

    private void gotoCreateGroup() {
        //Check if home already exists
        PECreateGroup peCreateGroup = (PECreateGroup) this.getActivity().getSupportFragmentManager().findFragmentById(R.id.pe_create_group_container);
        peCreateGroup = peCreateGroup == null ? new PECreateGroup() : peCreateGroup;

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.login_fragment_container, peCreateGroup);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
