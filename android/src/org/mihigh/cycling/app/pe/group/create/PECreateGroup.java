package org.mihigh.cycling.app.pe.group.create;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.utils.LoadingUtils;

import java.util.ArrayList;

public class PECreateGroup extends Fragment {

    private PEInvitedUserListAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        return inflater.inflate(R.layout.pe_group_create, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        setupEmailButton();
        setupIdButton();
        setupListViewAdapter();

        setupCreateButton();
        setupCancelButton();
    }


    private void setupIdButton() {
        getView().findViewById(R.id.pe_create_group_add_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(PECreateGroup.this.getActivity());

                alert.setTitle("Add User");
                alert.setMessage("Type id. The user id can be found in \"Join group\" menu");

                // Set an EditText view to get user input
                final EditText input = new EditText(PECreateGroup.this.getActivity());
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String email = input.getText().toString() + "@bikeroute.com";
                        adapter.insert(new PEInvitedUser(email), 0);
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

    private void setupEmailButton() {
        getView().findViewById(R.id.pe_create_group_add_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(PECreateGroup.this.getActivity());

                alert.setTitle("Add User");
                alert.setMessage("Type email");

                // Set an EditText view to get user input
                final EditText input = new EditText(PECreateGroup.this.getActivity());
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String email = input.getText().toString();
                        adapter.insert(new PEInvitedUser(email), 0);
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

    private void setupListViewAdapter() {
        adapter = new PEInvitedUserListAdapter(getActivity(), R.layout.pe_group_create_invite_user, new ArrayList<PEInvitedUser>());
        ListView atomPaysListView = (ListView) getView().findViewById(R.id.pe_create_group_invited_users);
        atomPaysListView.setAdapter(adapter);
    }

    private void setupCreateButton() {
        getView().findViewById(R.id.pe_create_group_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupName = ((EditText)getView().findViewById(R.id.pe_create_group_name)).getText().toString();


                int count = adapter.getCount();
                ArrayList<String> users = new ArrayList<String>(count);

                for (int i = 0; i < count; ++i) {
                    PEInvitedUser user = adapter.getItem(i);
                    users.add(user.email);
                }

                ProgressDialog progress = LoadingUtils.createLoadingDialog(getActivity());
                new Thread(new CreateGroupRunnable(groupName, users, PECreateGroup.this, progress)).start();
            }
        });
    }

    private void setupCancelButton() {
        getView().findViewById(R.id.pe_create_group_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
    }

}
