package org.mihigh.cycling.app.pe.group.join;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.login.dto.UserInfo;
import org.mihigh.cycling.app.pe.group.dto.PEGroupDetails;
import org.mihigh.cycling.app.pe.group.join.invitation.GetInvitationsRunnable;
import org.mihigh.cycling.app.pe.group.join.invitation.PEJoinInvitationListAdapter;
import org.mihigh.cycling.app.pe.group.join.search.GetGroupsRunnable;
import org.mihigh.cycling.app.pe.group.join.search.PESearchGroupListAdapter;

import java.util.ArrayList;

public class PEJoinGroup extends Fragment {

    private TextView noneText;
    private ListView inviteslist;
    private ListView seartchList;
    private SearchView search;
    private View searchBox;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        return inflater.inflate(R.layout.pe_group_join, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        setupSettings();
        setupInvites();
        setupSearch();

    }

    private void setupSettings() {
        getView().findViewById(R.id.pe_join_group_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(PEJoinGroup.this.getActivity());

                UserInfo userInfo = UserInfo.restore(getActivity());

                String label = "email";
                String value =  userInfo.getEmail();
                if (userInfo.isGenerated()) {
                    label = "id";
                    value = userInfo.getEmail().split("@")[0];
                }

                alert.setTitle("Your " + label);
                alert.setMessage(value);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

                alert.show();
            }
        });

    }

    private void setupSearch() {
        getView().findViewById(R.id.pe_join_group_search_group_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noneText.setVisibility(View.GONE);
                inviteslist.setVisibility(View.GONE);
                searchBox.setVisibility(View.VISIBLE);
                seartchList.setVisibility(View.VISIBLE);
            }
        });


        final PESearchGroupListAdapter adapter = new PESearchGroupListAdapter(getActivity(), R.layout.pe_group_join_search_result, new ArrayList<PEGroupDetails>());
        seartchList = (ListView) getView().findViewById(R.id.pe_join_group_search_list);
        seartchList.setAdapter(adapter);

        searchBox = getActivity().findViewById(R.id.pe_join_group_search_box);
        searchBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setFocusable(true);
                search.requestFocusFromTouch();
            }
        });

        search = (SearchView) getActivity().findViewById(R.id.pe_join_group_search);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                search.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                new Thread(new GetGroupsRunnable(getActivity(), adapter, newText)).start();
                return true;
            }

        });

    }

    private void setupInvites() {
        getView().findViewById(R.id.pe_join_group_invites_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noneText.setVisibility(View.GONE);
                inviteslist.setVisibility(View.VISIBLE);
                searchBox.setVisibility(View.GONE);
                seartchList.setVisibility(View.GONE);
            }
        });

        PEJoinInvitationListAdapter adapter = new PEJoinInvitationListAdapter(getActivity(), R.layout.pe_group_join_invitation, new ArrayList<PEGroupDetails>());
        inviteslist = (ListView) getView().findViewById(R.id.pe_join_group_invites_list);
        inviteslist.setAdapter(adapter);

        noneText = (TextView) getView().findViewById(R.id.pe_join_group_invites_none);

        new Thread(new GetInvitationsRunnable(getActivity(), adapter, noneText)).start();
    }
}
