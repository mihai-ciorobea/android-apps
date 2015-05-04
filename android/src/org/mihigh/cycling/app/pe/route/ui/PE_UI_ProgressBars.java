package org.mihigh.cycling.app.pe.route.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import org.mihigh.cycling.app.LoginActivity;
import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.Utils;
import org.mihigh.cycling.app.pe.route.tracking.UserTracking;

import java.util.Collections;
import java.util.List;

public class PE_UI_ProgressBars extends Fragment {

    private LinearLayout barLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.pe_route_activity_started_progress_bars, container, false);
        barLayout = (LinearLayout) view.findViewById(R.id.pe_route_activity_started_progress_bars_layout);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
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

    public void updateBars() {
        List<Integer> activity = UserTracking.instance.get5MinActivity();
        int size = activity.size();
        if (size > 9) {
            activity = activity.subList(size - 9, size);
        }

        int maxVal = Collections.max(activity);
        if (maxVal != 0) {
            for (int index = 0; index < activity.size(); ++index) {
                int distance = activity.get(index);

                int barSize = distance * 100 / maxVal;

                barLayout.getChildAt(index).getLayoutParams().height =
                        Math.max(Utils.getSizeFromDP(barSize, LoginActivity.scale), 10);
            }

            barLayout.requestLayout();
        }
    }

}
