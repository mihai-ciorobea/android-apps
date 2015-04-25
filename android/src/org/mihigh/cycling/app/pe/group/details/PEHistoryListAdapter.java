package org.mihigh.cycling.app.pe.group.details;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.pe.group.details.dto.Message;

import java.util.ArrayList;

public class PEHistoryListAdapter extends ArrayAdapter<Message> {

    private final int userLineResourceId;
    private final FragmentActivity activity;
    private final ArrayList<Message> items;

    public PEHistoryListAdapter(FragmentActivity activity, int userLineResourceId, ArrayList<Message> items) {
        super(activity, userLineResourceId, items);

        this.userLineResourceId = userLineResourceId;
        this.activity = activity;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = activity.getLayoutInflater().inflate(userLineResourceId, parent, false);

        final RowDataHolder holder = new RowDataHolder();
        holder.message = items.get(position);

        holder.userName = (TextView) row.findViewById(R.id.pe_group_home_msg_user);
        holder.userName.setText(holder.message.userInfo.getFirstName() + " " + holder.message.userInfo.getLastName());

        holder.image = (ImageView) row.findViewById(R.id.pe_group_home_msg_user_img);
        new Thread(new DownloadImageRunnable(activity, (ImageView) row.findViewById(R.id.pe_group_home_msg_user_img),
                holder.message.userInfo.getImageUrl())).start();

        holder.text = (TextView) row.findViewById(R.id.pe_group_home_msg_text);
        holder.text.setText(holder.message.text);


        row.setTag(holder);
        return row;
    }

    public static class RowDataHolder {
        Message message;
        TextView userName;
        TextView text;
        ImageView image;
    }
}

