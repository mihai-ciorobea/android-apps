package org.mihigh.cycling.app.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.facebook.AppEventsLogger;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import org.mihigh.cycling.app.LoginActivity;
import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.login.dto.UserInfo;

import java.util.Arrays;

public class LoginFragment extends Fragment {

    public static final String NO_IMAGE_URL = "http://kamomecorporation.com/image/default_main_image.jpg";

    private UiLifecycleHelper uiHelper;

    private Session.StatusCallback callback = new Session.StatusCallback() {

        @Override
        public void call(Session session, SessionState state, Exception exception) {
        }
    };
    private LoginActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = (LoginActivity) getActivity();

        uiHelper = new UiLifecycleHelper(activity, callback);
        uiHelper.onCreate(savedInstanceState);

        return inflater.inflate(R.layout.login_options, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        LoginButton loginButton = (LoginButton) getView().findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("email"));
        loginButton.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
            @Override
            public void onUserInfoFetched(GraphUser user) {
                if (user != null) {
                    String firstName = user.getFirstName();
                    String lastName = user.getLastName();
                    String id = user.getId();
                    String profilePic = "https://graph.facebook.com/" + id + "/picture?type=large";
                    String email = user.getProperty("email").toString();

                    UserInfo userInfo = new UserInfo(firstName, lastName, email, profilePic);
                    userInfo.store(activity);

                    new Thread(new MakeLoginRunnable(userInfo, activity)).start();
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }


    @Override
    public void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(activity);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(activity, requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }
}
