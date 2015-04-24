package org.mihigh.cycling.app.pe.group.create;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import org.mihigh.cycling.app.R;

import java.util.List;

public class PEInvitedUserListAdapter extends ArrayAdapter<PEInvitedUser> {

    private List<PEInvitedUser> items;
    private int invitedUserLayoutResourceId;
    private Activity activity;

    public PEInvitedUserListAdapter(Activity activity, int invitedUserLayoutResourceId, List<PEInvitedUser> items) {
        super(activity, invitedUserLayoutResourceId, items);
        this.invitedUserLayoutResourceId = invitedUserLayoutResourceId;
        this.activity = activity;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = activity.getLayoutInflater().inflate(invitedUserLayoutResourceId, parent, false);

        final PaymentHolder holder = new PaymentHolder();
        holder.PEInvitedUser = items.get(position);
        holder.removePaymentButton = (ImageButton) row.findViewById(R.id.pe_group_create_invite_user_remove);
        holder.removePaymentButton.setTag(holder.PEInvitedUser);
        holder.removePaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                items.remove(holder.PEInvitedUser);
                PEInvitedUserListAdapter.this.notifyDataSetChanged();
            }
        });
        holder.email = (TextView) row.findViewById(R.id.pe_group_create_invite_user_email);
        holder.email.setText(holder.PEInvitedUser.email);

        row.setTag(holder);
        return row;
    }

    public static class PaymentHolder {
        PEInvitedUser PEInvitedUser;
        TextView email;
        ImageButton removePaymentButton;
    }

}
