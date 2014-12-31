package org.mihigh.cycling.app.group;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.group.dto.JoinedRide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GroupJoinedListFragment extends Fragment {

    private ProgressDialog progress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.group_joined_list, container, false);
    }


    @Override
    public void onStart() {
        super.onStart();

        progress = new ProgressDialog(getActivity());
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.show();


        //TODO: save it
        new Thread(new GroupGetJoinedListRunnable(this)).start();
    }

    public void populateList(List<JoinedRide> values) {
        final ListView listview = (ListView) getActivity().findViewById(R.id.listview);

        final HashMap<String, JoinedRide> joinedMap = new HashMap<String, JoinedRide>();

        final ArrayList<String> list = new ArrayList<String>();
        for (JoinedRide joinedRide : values) {
            list.add(joinedRide.name);
            joinedMap.put(joinedRide.name, joinedRide);
        }
        final StableArrayAdapter adapter = new StableArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
                JoinedRide joinedRide = joinedMap.get(item);
                showRideDetails(joinedRide);
            }

        });

        progress.dismiss();

    }

    private void showRideDetails(JoinedRide joinedRide) {
        //Check if home already exists
        RideDetailsFragment fragment = (RideDetailsFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.group_ride_details);

        if (fragment != null) {
            fragment.updateHomeView();
        } else {
            RideDetailsFragment newFragment = new RideDetailsFragment();

            Bundle args = new Bundle();
            args.putSerializable(RideDetailsFragment.RIDE, joinedRide);
            newFragment.setArguments(args);

            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.login_fragment_container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void updateHomeView() {
        //TODO:
    }

    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }
}
