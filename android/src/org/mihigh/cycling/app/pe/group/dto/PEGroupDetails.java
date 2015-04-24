package org.mihigh.cycling.app.pe.group.dto;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class PEGroupDetails {

    public static final String SHARED_PREFERENCES_NAME = "GROUP_INFO";

    // Only store the fact that the user has a group
    public void setHasGroup(Activity activity, boolean hasGroup) {
        Log.e(PEGroupDetails.class.getName(), "Store hasGroup=" + hasGroup);

        SharedPreferences settings = activity.getSharedPreferences(PEGroupDetails.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putBoolean("hasGroup", hasGroup);

        editor.commit();
    }

    // Check if the user has a saved groups
    public static boolean getHasGroup(Activity activity) {
        SharedPreferences settings = activity.getSharedPreferences(PEGroupDetails.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

        return settings.getBoolean("hasGroup", false);
    }

}
