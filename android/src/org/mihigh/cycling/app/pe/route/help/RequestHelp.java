package org.mihigh.cycling.app.pe.route.help;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.utils.LoadingUtils;

public class RequestHelp extends Fragment {


    View.OnClickListener sendMsgListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button button = (Button) v;
                sendMsg(button.getText().toString(), checkBoxGroup.isChecked(), checkBoxNearby.isChecked());
        }
    };

    private CheckBox checkBoxGroup;
    private CheckBox checkBoxNearby;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        return inflater.inflate(R.layout.pe_route_activity_started_help, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        checkBoxGroup = (CheckBox) getView().findViewById(R.id.pe_route_activity_started_help_checkbox_group);
        checkBoxNearby = (CheckBox) getView().findViewById(R.id.pe_route_activity_started_help_checkbox_nearby);

        getView().findViewById(R.id.pe_route_activity_started_help_predifined_msg1).setOnClickListener(sendMsgListener);
        getView().findViewById(R.id.pe_route_activity_started_help_predifined_msg2).setOnClickListener(sendMsgListener);
        getView().findViewById(R.id.pe_route_activity_started_help_predifined_msg3).setOnClickListener(sendMsgListener);
        getView().findViewById(R.id.pe_route_activity_started_help_predifined_msg4).setOnClickListener(sendMsgListener);
        getView().findViewById(R.id.pe_route_activity_started_help_predifined_msg5).setOnClickListener(sendMsgListener);
        getView().findViewById(R.id.pe_route_activity_started_help_predifined_msg6).setOnClickListener(sendMsgListener);


        final EditText text = (EditText) getView().findViewById(R.id.pe_route_activity_started_help_text);
        text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    sendMsg(text.getText().toString(), checkBoxGroup.isChecked(), checkBoxNearby.isChecked());
                    text.setText("");
                }
                return false;
            }
        });

        getView().findViewById(R.id.pe_route_activity_started_help_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMsg(text.getText().toString(), checkBoxGroup.isChecked(), checkBoxNearby.isChecked());
                text.setText("");
            }
        });
    }

    private void sendMsg(String text, boolean group, boolean nearby) {
        ProgressDialog loadingDialog = LoadingUtils.createLoadingDialog(getActivity());
        new Thread(new NeedHelpRunnable(getActivity(), text, group, nearby, loadingDialog)).start();
    }
}
