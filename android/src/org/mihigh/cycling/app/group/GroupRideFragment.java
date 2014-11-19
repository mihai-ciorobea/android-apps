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
    public ScreenSlidePagerAdapter mPagerAdapter;
    public long id;

    public GroupRideFragment(long id) {

        this.id = id;
    }

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
            mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager(), this);
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

    public void updateHomeView(long id) {
        //TODO:
    }

    public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        private final GroupRideFragment groupRideFragment;
        private GroupHomeFragment groupHomeFragment;
        private GroupMapFragment groupMapFragment;

        public ScreenSlidePagerAdapter(FragmentManager fm, GroupRideFragment groupRideFragment) {
            super(fm);
            this.groupRideFragment = groupRideFragment;
            groupMapFragment = new GroupMapFragment(groupRideFragment.id, mPager);
            groupHomeFragment = new GroupHomeFragment(groupRideFragment);
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

        public GroupMapFragment getMap() {
            return groupMapFragment;
        }
    }


}
