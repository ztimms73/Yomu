package org.xtimms.yomu.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.navigation.NavigationView;
import com.kabouzeid.appthemehelper.ThemeStore;
import com.kabouzeid.appthemehelper.util.ATHUtil;
import com.kabouzeid.appthemehelper.util.NavigationViewUtil;

import org.xtimms.yomu.R;
import org.xtimms.yomu.ui.base.AbsBaseActivity;
import org.xtimms.yomu.ui.fragments.main.LibraryFragment;
import org.xtimms.yomu.util.PreferenceUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AbsBaseActivity {

    private static final int LIBRARY = 0;

    @BindView(R.id.navigation_view)
    NavigationView navigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @Nullable
    Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDrawUnderStatusbar();
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            navigationView.setFitsSystemWindows(false); // for header to go below statusbar
        }

        setUpDrawerLayout();

        if (savedInstanceState == null) {
            setChooser(PreferenceUtil.getInstance(this).getLastChooser());
        } else {
            restoreCurrentFragment();
        }
    }

    private void setChooser(int key) {
        PreferenceUtil.getInstance(this).setLastChooser(key);
        switch (key) {
            case LIBRARY:
                navigationView.setCheckedItem(R.id.nav_library);
                setCurrentFragment(LibraryFragment.newInstance());
                break;
        }
    }

    private void setCurrentFragment(@SuppressWarnings("NullableProblems") Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, null).commit();
        currentFragment = fragment;
    }

    private void restoreCurrentFragment() {
        currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
    }

    @Override
    protected View createContentView() {
        @SuppressLint("InflateParams")
        View contentView = getLayoutInflater().inflate(R.layout.activity_main_drawer_layout, null);
        return contentView;
    }

    private void setUpNavigationView() {
        int accentColor = ThemeStore.accentColor(this);
        NavigationViewUtil.setItemIconColors(navigationView, ATHUtil.resolveColor(this, R.attr.iconColor, ThemeStore.textColorSecondary(this)), accentColor);
        NavigationViewUtil.setItemTextColors(navigationView, ThemeStore.textColorPrimary(this), accentColor);

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            drawerLayout.closeDrawers();
            int id = menuItem.getItemId();
            if (id == R.id.nav_settings) {
                new Handler().postDelayed(() -> startActivity(new Intent(MainActivity.this, SettingsActivity.class)), 200);
            } else if (id == R.id.nav_about) {
                new Handler().postDelayed(() -> startActivity(new Intent(MainActivity.this, AboutActivity.class)), 200);
            }
            return true;
        });
    }

    private void setUpDrawerLayout() {
        setUpNavigationView();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(navigationView)) {
                drawerLayout.closeDrawer(navigationView);
            } else {
                drawerLayout.openDrawer(navigationView);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}