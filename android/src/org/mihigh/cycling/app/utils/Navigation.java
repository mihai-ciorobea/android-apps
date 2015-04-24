package org.mihigh.cycling.app.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import org.mihigh.cycling.app.R;

public class Navigation {

    public static void changeFragment(FragmentActivity activity, int resourceId, Fragment fragment) {
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.login_fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
