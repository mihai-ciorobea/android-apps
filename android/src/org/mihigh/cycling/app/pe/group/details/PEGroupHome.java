package org.mihigh.cycling.app.pe.group.details;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.mihigh.cycling.app.R;

public class PEGroupHome extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        return inflater.inflate(R.layout.pe_group_home, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

    }
}
