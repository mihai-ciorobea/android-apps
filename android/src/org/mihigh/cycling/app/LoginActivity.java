package org.mihigh.cycling.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import org.mihigh.cycling.app.solo.SoloRideFragment;

public class LoginActivity extends FragmentActivity {

    private String userName;
    public static float scale;
    private FacebookFragment facebookFragment;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scale = getResources().getDisplayMetrics().density;
        setContentView(R.layout.login);

        if (findViewById(R.id.login_fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            facebookFragment = new FacebookFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.login_fragment_container, facebookFragment).commit();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.login_fragment_container, facebookFragment);
            transaction.addToBackStack(null);
            transaction.commit();
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


    public void onUserLoggedIn(String userName) {
        this.userName = userName;

        //Check if home already exists
        HomeFragment fragment = (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.home_fragment_container);


        if (fragment != null) {
            fragment.updateHomeView(userName);
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
        HomeFragment fragment = (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.solo_ride_fragment_container);

        if (fragment != null) {
            fragment.updateHomeView(userName);
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



}
