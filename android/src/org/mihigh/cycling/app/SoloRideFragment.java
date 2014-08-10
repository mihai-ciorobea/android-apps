package org.mihigh.cycling.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SoloRideFragment extends Fragment {


    private ViewPager mPager;
    private ScreenSlidePagerAdapter mPagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.solo_ride, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) getView().findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
        mPager.setAdapter(mPagerAdapter);

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        private SoloHomeFragment soloHomeFragment;
        private SoloMapFragment soloMapFragment;

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                soloHomeFragment = new SoloHomeFragment();
                return soloHomeFragment;
            }

            soloMapFragment = new SoloMapFragment();
            return soloMapFragment;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }


}
