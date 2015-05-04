package org.mihigh.cycling.app.pe.group.details.settings;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.pe.group.dto.PEGroupDetails;
import org.mihigh.cycling.app.utils.LoadingUtils;

import java.util.ArrayList;

public class PEGroupHomeSettings extends Fragment {


    private PEGroupDetails groupDetails;

    public PEGroupHomeSettings(PEGroupDetails groupDetails) {

        this.groupDetails = groupDetails;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        return inflater.inflate(R.layout.pe_group_home_settings, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        setupLeaveButton();
        setupMembersButton();
        setupInviteButton();
        setupRequestsButton();

    }

    private void setupRequestsButton() {
        final PEJoinRequestListAdapter adapter = new PEJoinRequestListAdapter(getActivity(), R.layout.pe_group_join_invitation, new ArrayList<RequestData>());
        final ListView requestlist = (ListView) getView().findViewById(R.id.pe_group_details_settings_requests_list);
        requestlist.setAdapter(adapter);

        getView().findViewById(R.id.pe_group_details_settings_requests).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideAll();
                requestlist.setVisibility(View.VISIBLE);

                ProgressDialog loadingDialog = LoadingUtils.createLoadingDialog(getActivity());
                new Thread(new GetRequestsRunnable(getActivity(), groupDetails, adapter, loadingDialog)).start();
            }
        });
    }

    private void setupInviteButton() {
        final LinearLayout inviteButtons = (LinearLayout) getView().findViewById(R.id.pe_group_details_settings_add_buttons);
        final ListView inviteList = (ListView) getView().findViewById(R.id.pe_group_details_settings_invited_users_list);

        final ArrayAdapter<String> invitesAdapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, new ArrayList<String>());

        inviteList.setAdapter(invitesAdapter);


        getView().findViewById(R.id.pe_group_details_settings_invite_user).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideAll();
                inviteButtons.setVisibility(View.VISIBLE);
                inviteList.setVisibility(View.VISIBLE);

            }
        });

        setupEmailButton(invitesAdapter);
        setupIdButton(invitesAdapter);
    }


    private void setupIdButton(final ArrayAdapter<String> invitesAdapter) {
        getView().findViewById(R.id.pe_group_details_settings_add_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(PEGroupHomeSettings.this.getActivity());

                alert.setTitle("Add User");
                alert.setMessage("Type id. The user id can be found in \"Prima Evadare\" menu");

                // Set an EditText view to get user input
                final EditText input = new EditText(PEGroupHomeSettings.this.getActivity());
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String id = input.getText().toString();
                        String email = id + "@bikeroute.com";

                        ProgressDialog loadingDialog = LoadingUtils.createLoadingDialog(getActivity());
                        new Thread(new InviteUserRunnable(getActivity(), groupDetails, email, loadingDialog, invitesAdapter, id)).start();
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();
            }
        });
    }

    private void setupEmailButton(final ArrayAdapter<String> invitesAdapter) {
        getView().findViewById(R.id.pe_group_details_settings_add_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(PEGroupHomeSettings.this.getActivity());

                alert.setTitle("Add User");
                alert.setMessage("Type email");

                // Set an EditText view to get user input
                final EditText input = new EditText(PEGroupHomeSettings.this.getActivity());
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String email = input.getText().toString();

                        ProgressDialog loadingDialog = LoadingUtils.createLoadingDialog(getActivity());
                        new Thread(new InviteUserRunnable(getActivity(), groupDetails, email, loadingDialog, invitesAdapter, email)).start();
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();
            }
        });
    }


    private void hideAll() {

        // Members
        getView().findViewById(R.id.pe_group_details_settings_members_list).setVisibility(View.GONE);

        // Invite
        getView().findViewById(R.id.pe_group_details_settings_add_buttons).setVisibility(View.GONE);
        getView().findViewById(R.id.pe_group_details_settings_invited_users_list).setVisibility(View.GONE);

        // Requests
        getView().findViewById(R.id.pe_group_details_settings_requests_list).setVisibility(View.GONE);

    }

    private void setupMembersButton() {
        final ArrayAdapter<String> membersAdapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, new ArrayList<String>());

        final ListView members = (ListView) getView().findViewById(R.id.pe_group_details_settings_members_list);
        members.setAdapter(membersAdapter);

        getView().findViewById(R.id.pe_group_details_settings_members).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideAll();
                members.setVisibility(View.VISIBLE);
                ProgressDialog loadingDialog = LoadingUtils.createLoadingDialog(getActivity());
                new Thread(new GetGroupMembersRunnable(getActivity(), groupDetails, membersAdapter, loadingDialog)).start();
            }
        });

        hideAll();
        members.setVisibility(View.VISIBLE);
        ProgressDialog loadingDialog = LoadingUtils.createLoadingDialog(getActivity());
        new Thread(new GetGroupMembersRunnable(getActivity(), groupDetails, membersAdapter, loadingDialog)).start();

    }

    private void setupLeaveButton() {
        getView().findViewById(R.id.pe_group_details_settings_leave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new LeaveGroupRunnable(getActivity(), groupDetails)).start();
            }
        });
    }


}
