package org.mihigh.cycling.app.group;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.mihigh.cycling.app.R;

public class GroupRideFragment extends Fragment {


    private ViewPager mPager;
    private ScreenSlidePagerAdapter mPagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.group_ride, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) getActivity().findViewById(R.id.group_pager);

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

    public void updateHomeView() {
        //TODO:
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        private GroupHomeFragment groupHomeFragment = new GroupHomeFragment();
        private GroupMapFragment groupMapFragment = new GroupMapFragment();

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return groupHomeFragment;
            }

            return groupMapFragment;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }


}
