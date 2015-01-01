package org.mihigh.cycling.app.group;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.mihigh.cycling.app.LoginActivity;
import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.Utils;
import org.mihigh.cycling.app.group.dto.ProgressStatus;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GroupProgressBars extends Fragment {

    private LinearLayout barLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.group_progress_bars, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        barLayout = (LinearLayout) getView().findViewById(R.id.performance_bars);
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

    private void updateBars() {
        List<Integer> activity = GroupTracking.instance.get5MinActivity();
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
