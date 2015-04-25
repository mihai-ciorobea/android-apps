package org.mihigh.cycling.app.pe.group.details.settings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.pe.group.dto.PEGroupDetails;

public class PEGroupHomeSettings extends Fragment {


    private PEGroupDetails groupDetails;

    public PEGroupHomeSettings(PEGroupDetails groupDetails) {

        this.groupDetails = groupDetails;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        return inflater.inflate(R.layout.pe_group_home_settings, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        setupLeaveButton();

    }

    private void setupLeaveButton() {
        getView().findViewById(R.id.pe_group_details_settings_leave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new LeaveGroupRunnable(getActivity(), groupDetails));
            }
        });
    }


}
