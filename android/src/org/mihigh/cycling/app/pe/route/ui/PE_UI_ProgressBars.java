package org.mihigh.cycling.app.pe.route.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import org.mihigh.cycling.app.R;

public class PE_UI_ProgressBars extends Fragment {

    private LinearLayout barLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.pe_route_activity_started_progress_bars, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        barLayout = (LinearLayout) getView().findViewById(R.id.pe_route_activity_started_progress_bars_layout);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
