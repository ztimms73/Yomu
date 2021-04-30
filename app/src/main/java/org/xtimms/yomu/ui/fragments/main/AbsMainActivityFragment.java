package org.xtimms.yomu.ui.fragments.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import org.xtimms.yomu.ui.activities.MainActivity;

public abstract class AbsMainActivityFragment extends Fragment {

    public MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }
}