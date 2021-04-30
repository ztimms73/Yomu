package org.xtimms.yomu.ui.fragments.main;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.kabouzeid.appthemehelper.ThemeStore;

import org.xtimms.yomu.R;
import org.xtimms.yomu.util.PreferenceUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class LibraryFragment extends AbsMainActivityFragment implements SharedPreferences.OnSharedPreferenceChangeListener, ViewPager.OnPageChangeListener {

    private Unbinder unbinder;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.pager)
    ViewPager pager;

    public static LibraryFragment newInstance() {
        return new LibraryFragment();
    }

    public LibraryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        PreferenceUtil.getInstance(getActivity()).unregisterOnSharedPreferenceChangedListener(this);
        super.onDestroyView();
        pager.removeOnPageChangeListener(this);
        unbinder.unbind();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        PreferenceUtil.getInstance(getActivity()).registerOnSharedPreferenceChangedListener(this);
        getMainActivity().setStatusbarColorAuto();
        getMainActivity().setNavigationbarColorAuto();
        getMainActivity().setTaskDescriptionColorAuto();

        setUpToolbar();
    }

    private void setUpToolbar() {
        int primaryColor = ThemeStore.primaryColor(getActivity());
        appbar.setBackgroundColor(primaryColor);
        toolbar.setBackgroundColor(primaryColor);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        getActivity().setTitle(R.string.app_name);
        getMainActivity().setSupportActionBar(toolbar);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        PreferenceUtil.getInstance(getActivity()).setLastPage(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
