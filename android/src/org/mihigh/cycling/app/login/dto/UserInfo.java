package org.mihigh.cycling.app.login.dto;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class UserInfo {
    public static String SHARED_PREFERENCES_NAME = "USER_INFO";

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String imageUrl;

    public UserInfo() {
    }

    public UserInfo(String firstName, String lastName, String email, String imageUrl) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.imageUrl = imageUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isGenerated() {
        return email.contains("bikeroute");
    }

    public String getUIName() {
        return isGenerated() ?
                email.split("@")[0] :
                firstName + " " + lastName;
    }

    public void store(Activity activity) {
        Log.e(UserInfo.class.getName(), "Store " + this.toString());

        SharedPreferences settings = activity.getSharedPreferences(UserInfo.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString("firstName", firstName);
        editor.putString("lastName", lastName);
        editor.putString("email", email);
        editor.putString("imageUrl", imageUrl);

        editor.commit();
    }

    public static UserInfo restore(Activity activity) {
        SharedPreferences settings = activity.getSharedPreferences(UserInfo.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

        if (settings.getString("email", null) == null) {
            return null;
        }

        UserInfo userInfo = new UserInfo(settings.getString("firstName", null),
                settings.getString("lastName", null),
                settings.getString("email", null),
                settings.getString("imageUrl", null));

        Log.e(UserInfo.class.getName(), "Restore " + userInfo.toString());

        return userInfo;
    }

    public void clearStore(Activity activity) {
        Log.e(UserInfo.class.getName(), "Clear user" + this.toString());

        SharedPreferences settings = activity.getSharedPreferences(UserInfo.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString("firstName", null);
        editor.putString("lastName", null);
        editor.putString("email", null);
        editor.putString("imageUrl", null);

        editor.commit();

    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
