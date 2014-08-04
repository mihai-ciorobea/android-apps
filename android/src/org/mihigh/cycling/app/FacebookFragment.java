package org.mihigh.cycling.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

public class FacebookFragment extends Fragment {

    private LoginButton loginButton;
    private GraphUser user;
    private TextView greeting;

    private UiLifecycleHelper uiHelper;

    private Session.StatusCallback callback = new Session.StatusCallback() {
        public void call(Session session, SessionState state, Exception exception) {
            if (state.isOpened()) {
                updateUI();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);

        return inflater.inflate(R.layout.facebook, container, false);
    }


    @Override
    public void onStart() {
        super.onStart();

        greeting = (TextView) getView().findViewById(R.id.greeting);

        loginButton = (LoginButton) getView().findViewById(R.id.login_button);
        loginButton.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
            @Override
            public void onUserInfoFetched(GraphUser user) {
                FacebookFragment.this.user = user;
                updateUI();
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

    private void updateUI() {
        Session session = Session.getActiveSession();
        boolean enableButtons = (session != null && session.isOpened());

        if (enableButtons) {
            if (user != null) {
                greeting.setText(user.getName());
            }
            loginButton.setVisibility(View.INVISIBLE);
        } else {
            loginButton.setVisibility(View.VISIBLE);
            greeting.setText(null);

        }
    }

}
