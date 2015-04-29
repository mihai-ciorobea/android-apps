package org.mihigh.cycling.app.pe.route.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import org.mihigh.cycling.app.pe.route.tracking.gps.PE_GPS_Service;

import java.util.ArrayList;
import java.util.List;

public class PE_UI_TracingViewPageAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> pages = new ArrayList<Fragment>();

    public PE_UI_TracingViewPageAdapter(FragmentManager fm, ViewPager viewPager, PE_GPS_Service gpsService) {
        super(fm);
        pages.add(new PE_UI_ProgressBars());
        pages.add(new PE_UI_MapFragment(viewPager, gpsService));
        pages.add(new PE_UI_Configure());
    }

    @Override
    public Fragment getItem(int position) {
        return pages.get(position);
    }

    @Override
    public int getCount() {
        return 3;
    }

//    public PE_UI_MapFragment getMap() {
//        return (PE_UI_MapFragment) pages.get(getMapIndex());
//    }

    public int getMapIndex() {
        return 1;
    }
}
