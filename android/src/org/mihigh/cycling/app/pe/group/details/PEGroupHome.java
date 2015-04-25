package org.mihigh.cycling.app.pe.group.details;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.pe.group.details.dto.Message;
import org.mihigh.cycling.app.pe.group.details.settings.PEGroupHomeSettings;
import org.mihigh.cycling.app.pe.group.dto.PEGroupDetails;
import org.mihigh.cycling.app.utils.LoadingUtils;
import org.mihigh.cycling.app.utils.Navigation;

import java.util.ArrayList;

public class PEGroupHome extends Fragment {

    private PEHistoryListAdapter adapter;
    private PEGroupDetails groupDetails;
    private ProgressDialog loadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        return inflater.inflate(R.layout.pe_group_home, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        setupSettings();

        loadingDialog = LoadingUtils.createLoadingDialog(getActivity());
        new Thread(new PEGroupDetailsRunnable(this)).start();
    }

    private void setupSettings() {
        getView().findViewById(R.id.pe_group_home_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.changeFragment(getActivity(), R.id.login_fragment_container, new PEGroupHomeSettings(groupDetails));
            }
        });
    }

    private void setupSendNewText() {
        final EditText text = (EditText) getView().findViewById(R.id.pe_group_home_new_text);
        text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    new Thread(new SendMsgRunnable(getActivity(), adapter, text.getText().toString(), groupDetails)).start();
                    text.setText("");
                }
                return false;
            }
        });


        ImageButton send = (ImageButton) getView().findViewById(R.id.pe_group_home_send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new SendMsgRunnable(getActivity(), adapter, text.getText().toString(), groupDetails)).start();
                text.setText("");
            }
        });

    }

    private void setupHistory() {
        // Adapter
        adapter = new PEHistoryListAdapter(getActivity(), R.layout.pe_group_home_msg, new ArrayList<Message>());
        ListView historyList = (ListView) getView().findViewById(R.id.pe_group_home_history);
        historyList.setAdapter(adapter);

        TextView noHistory = (TextView) getView().findViewById(R.id.pe_group_home_no_history);

        // Populate list
        new Thread(new GetHistoryRunnable(getActivity(), adapter, noHistory, groupDetails, loadingDialog, historyList)).start();
    }

    public void updateDetails(PEGroupDetails peGroupDetails) {
        this.groupDetails = peGroupDetails;
        setupHistory();
        setupSendNewText();
    }



}
