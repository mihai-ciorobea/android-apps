package org.mihigh.cycling.app.solo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.mihigh.cycling.app.R;

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
        mPager = (ViewPager) getActivity().findViewById(R.id.pager);

        if (mPagerAdapter == null) {
            mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
        }
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

    @Override
    public void onResume() {
        super.onResume();
    }

    public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        private SoloHomeFragment soloHomeFragment = new SoloHomeFragment();
        private SoloMapFragment soloMapFragment = new SoloMapFragment(mPager);

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return soloHomeFragment;
            }

            return soloMapFragment;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }


}
