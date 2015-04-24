package org.mihigh.cycling.app.pe;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class PEGroupDetails {

    public static final String SHARED_PREFERENCES_NAME = "GROUP_INFO";

    public String name;
    public long id;


    public PEGroupDetails(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public void store(Activity activity) {
        Log.e(PEGroupDetails.class.getName(), "Store " + this.toString());

        SharedPreferences settings = activity.getSharedPreferences(PEGroupDetails.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putLong("id", id);
        editor.putString("name", name);

        editor.commit();
    }

    public static PEGroupDetails restore(Activity activity) {
        SharedPreferences settings = activity.getSharedPreferences(PEGroupDetails.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

        if (settings.getString("id", null) == null) {
            return null;
        }

        PEGroupDetails groupDetails = new PEGroupDetails(settings.getLong("id", -1), settings.getString("name", null));

        Log.e(PEGroupDetails.class.getName(), "Restored " + groupDetails.toString());

        return groupDetails;
    }


    public void clearStore(Activity activity) {
        Log.e(PEGroupDetails.class.getName(), "Clear group details" + this.toString());

        SharedPreferences settings = activity.getSharedPreferences(PEGroupDetails.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString("id", null);
        editor.putString("name", null);

        editor.commit();
    }


    @Override
    public String toString() {
        return "PEGroupDetails{" +
                "name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}
