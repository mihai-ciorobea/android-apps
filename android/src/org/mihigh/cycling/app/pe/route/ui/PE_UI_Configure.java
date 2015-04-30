package org.mihigh.cycling.app.pe.route.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.pe.route.tracking.UserTracking;

public class PE_UI_Configure extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.pe_route_activity_started_configure, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        final CheckBox groupCheckBox = (CheckBox) getView().findViewById(R.id.pe_route_activity_stated_configuration_group);
        groupCheckBox.setChecked(UserTracking.instance.setGroupVisibility);
        groupCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserTracking.instance.setGroupVisibility = groupCheckBox.isChecked();
            }
        });

        final CheckBox nearbyCheckBox = (CheckBox) getView().findViewById(R.id.pe_route_activity_stated_configuration_nearby);
        nearbyCheckBox.setChecked(UserTracking.instance.setNearbyVisibility);
        nearbyCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserTracking.instance.setNearbyVisibility = nearbyCheckBox.isChecked();
            }
        });
    }
}
