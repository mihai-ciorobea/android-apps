package org.mihigh.cycling.app.pe.group.details.settings;

import android.app.ProgressDialog;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.pe.group.join.invitation.InvitationUpdateRunnable;
import org.mihigh.cycling.app.utils.LoadingUtils;

import java.util.ArrayList;

public class PEJoinRequestListAdapter extends ArrayAdapter<RequestData> {

    private final int invitedUserLayoutResourceId;
    private final FragmentActivity activity;
    private final ArrayList<RequestData> items;

    public PEJoinRequestListAdapter(FragmentActivity activity, int invitedUserLayoutResourceId, ArrayList<RequestData> items) {
        super(activity, invitedUserLayoutResourceId, items);

        this.invitedUserLayoutResourceId = invitedUserLayoutResourceId;
        this.activity = activity;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = activity.getLayoutInflater().inflate(invitedUserLayoutResourceId, parent, false);

        final RowDataHolder holder = new RowDataHolder();
        holder.requestData = items.get(position);

        holder.removeButton = (ImageButton) row.findViewById(R.id.pe_group_join_delete_invitation);
        holder.removeButton.setTag(holder.requestData);
        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //HTTP call delete
                ProgressDialog progress = LoadingUtils.createLoadingDialog(activity);
                new Thread(new RequestUpdateRunnable(holder.requestData, InvitationUpdateRunnable.Action.DELETE,
                        activity, PEJoinRequestListAdapter.this, progress)).start();

            }
        });

        holder.acceptButton = (ImageButton) row.findViewById(R.id.pe_group_join_accept_invitation);
        holder.acceptButton.setTag(holder.requestData);
        holder.acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //HTTP call to accept
                ProgressDialog progress = LoadingUtils.createLoadingDialog(activity);
                new Thread(new RequestUpdateRunnable(holder.requestData, InvitationUpdateRunnable.Action.ACCEPT,
                        activity, PEJoinRequestListAdapter.this, progress)).start();

                activity.getFragmentManager().popBackStack();
            }
        });

        holder.emailTextView = (TextView) row.findViewById(R.id.pe_group_join_groupName);
        holder.emailTextView.setText(holder.requestData.userInfo.getUIName());

        row.setTag(holder);
        return row;
    }

    public void deleteInvitation(RequestData groupDetails) {
        items.remove(groupDetails);
        PEJoinRequestListAdapter.this.notifyDataSetChanged();
    }

    public static class RowDataHolder {
        RequestData requestData;
        TextView emailTextView;
        ImageButton acceptButton;
        ImageButton removeButton;
    }

}

