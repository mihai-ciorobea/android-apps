package org.mihigh.cycling.app.pe.group.join.invitation;

import android.app.ProgressDialog;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.pe.group.dto.PEGroupDetails;
import org.mihigh.cycling.app.utils.LoadingUtils;

import java.util.ArrayList;

public class PEJoinInvitationListAdapter extends ArrayAdapter<PEGroupDetails> {

    private final int invitedUserLayoutResourceId;
    private final FragmentActivity activity;
    private final ArrayList<PEGroupDetails> items;

    public PEJoinInvitationListAdapter(FragmentActivity activity, int invitedUserLayoutResourceId, ArrayList<PEGroupDetails> items) {
        super(activity, invitedUserLayoutResourceId, items);

        this.invitedUserLayoutResourceId = invitedUserLayoutResourceId;
        this.activity = activity;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = activity.getLayoutInflater().inflate(invitedUserLayoutResourceId, parent, false);

        final RowDataHolder holder = new RowDataHolder();
        holder.groupDetails = items.get(position);

        holder.removeButton = (ImageButton) row.findViewById(R.id.pe_group_join_delete_invitation);
        holder.removeButton.setTag(holder.groupDetails);
        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //HTTP call delete
                ProgressDialog progress = LoadingUtils.createLoadingDialog(activity);
                new Thread(new InvitationUpdateRunnable(holder.groupDetails, InvitationUpdateRunnable.Action.DELETE,
                        activity, PEJoinInvitationListAdapter.this, progress)).start();

            }
        });

        holder.acceptButton = (ImageButton) row.findViewById(R.id.pe_group_join_accept_invitation);
        holder.acceptButton.setTag(holder.groupDetails);
        holder.acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //HTTP call to accept
                ProgressDialog progress = LoadingUtils.createLoadingDialog(activity);
                new Thread(new InvitationUpdateRunnable(holder.groupDetails, InvitationUpdateRunnable.Action.ACCEPT,
                        activity, PEJoinInvitationListAdapter.this, progress)).start();

                activity.getFragmentManager().popBackStack();
            }
        });

        holder.groupName = (TextView) row.findViewById(R.id.pe_group_join_groupName);
        holder.groupName.setText(holder.groupDetails.name);

        row.setTag(holder);
        return row;
    }

    public void deleteInvitation(PEGroupDetails groupDetails) {
        items.remove(groupDetails);
        PEJoinInvitationListAdapter.this.notifyDataSetChanged();
    }

    public static class RowDataHolder {
        PEGroupDetails groupDetails;
        TextView groupName;
        ImageButton acceptButton;
        ImageButton removeButton;
    }
}
