package org.mihigh.cycling.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import org.mihigh.cycling.app.filter.ExceptionHandler;
import org.mihigh.cycling.app.group.GroupJoinedListFragment;
import org.mihigh.cycling.app.group.GroupMeniuFragment;
import org.mihigh.cycling.app.group.GroupResult;
import org.mihigh.cycling.app.group.GroupRideFragment;
import org.mihigh.cycling.app.group.SearchListFragment;
import org.mihigh.cycling.app.login.FacebookFragment;
import org.mihigh.cycling.app.login.dto.UserInfo;
import org.mihigh.cycling.app.solo.SoloResult;
import org.mihigh.cycling.app.solo.SoloRideFragment;

public class LoginActivity extends FragmentActivity {

    private String userName;
    public static float scale;
    private FacebookFragment facebookFragment;
    private GroupMeniuFragment groupMeniuFragment;
    public static UserInfo userInfo;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        scale = getResources().getDisplayMetrics().density;
        setContentView(R.layout.login);

        if (findViewById(R.id.login_fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            if (!isNetworkConnected()) {
                buildAlertMessageNoInternet();
            }

            facebookFragment = new FacebookFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.login_fragment_container, facebookFragment).commit();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.login_fragment_container, facebookFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }


    private void buildAlertMessageNoInternet() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your MobileData seems to be disabled, do you want to enable it? The app will not work without it.")
            .setCancelable(false)
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                    startActivity(new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS));
                }
            })
            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                    dialog.cancel();
                }
            });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebookFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void updateUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public void onUserLoggedIn() {
        //Check if home already exists
        HomeFragment fragment = (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.home_fragment_container);

        if (fragment != null) {
            fragment.updateHomeView();
        } else {
            HomeFragment newFragment = new HomeFragment();
            Bundle args = new Bundle();
            args.putString(HomeFragment.USER, userName);
            newFragment.setArguments(args);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.login_fragment_container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    public void startSoloRide() {
        //Check if home already exists
        HomeFragment fragment = (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.group_ride_fragment_container);

        if (fragment != null) {
            fragment.updateHomeView();
        } else {
            SoloRideFragment newFragment = new SoloRideFragment();
            Bundle args = new Bundle();
            args.putString(HomeFragment.USER, userName);
            newFragment.setArguments(args);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.login_fragment_container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }

    }

    public void stopRide() {
        SoloResult fragment = (SoloResult) getSupportFragmentManager().findFragmentById(R.id.solo_result);

        if (fragment != null) {
            fragment.update();
        } else {
            SoloResult newFragment = new SoloResult();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.login_fragment_container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }

    }

    public void stopGroupRide(GroupRideFragment groupRideFragment) {
        GroupResult fragment = (GroupResult) getSupportFragmentManager().findFragmentById(R.id.group_result);

        if (fragment != null) {
            fragment.update();
        } else {
            GroupResult newFragment = new GroupResult();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.login_fragment_container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }

        groupRideFragment.mPagerAdapter.getMap().removeListner();

    }

    public void startGroupRide() {
        //Check if home already exists
        groupMeniuFragment = (GroupMeniuFragment) getSupportFragmentManager().findFragmentById(R.id.group_fragment_container);

        if (groupMeniuFragment != null) {
            groupMeniuFragment.updateHomeView();
        } else {
            groupMeniuFragment = new GroupMeniuFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.login_fragment_container, groupMeniuFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    public void searchForRide() {

        //Check if home already exists
        SearchListFragment fragment = (SearchListFragment) getSupportFragmentManager().findFragmentById(R.id.listview);

        if (fragment != null) {
            fragment.updateHomeView();
        } else {
            SearchListFragment newFragment = new SearchListFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.login_fragment_container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    public void joinedRides() {
        //Check if home already exists
        GroupJoinedListFragment fragment = (GroupJoinedListFragment) getSupportFragmentManager().findFragmentById(R.id.listview);

        if (fragment != null) {
            fragment.updateHomeView();
        } else {
            GroupJoinedListFragment newFragment = new GroupJoinedListFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.login_fragment_container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }

        //TODO:
    }
}

