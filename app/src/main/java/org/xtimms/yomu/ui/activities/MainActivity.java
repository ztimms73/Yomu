package org.xtimms.yomu.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.kabouzeid.appthemehelper.ThemeStore;
import com.kabouzeid.appthemehelper.util.ATHUtil;
import com.kabouzeid.appthemehelper.util.NavigationViewUtil;

import org.xtimms.yomu.R;
import org.xtimms.yomu.listeners.OnTipsActionListener;
import org.xtimms.yomu.misc.UpdateHelper;
import org.xtimms.yomu.ui.base.AbsBaseActivity;
import org.xtimms.yomu.ui.fragments.main.explore.ExploreFragment;
import org.xtimms.yomu.ui.fragments.main.library.LibraryFragment;
import org.xtimms.yomu.util.PreferenceUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AbsBaseActivity implements OnTipsActionListener, UpdateHelper.OnUpdateCheckListener {

    private static final int LIBRARY = 0;
    private static final int EXPLORE = 1;

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
            if (PreferenceUtil.getInstance(this).rememberLastPage()) {
                setChooser(PreferenceUtil.getInstance(this).getLastChooser());
            } else {
                setChooser(LIBRARY);
            }
        } else {
            restoreCurrentFragment();
        }

        UpdateHelper.with(this).onUpdateCheck(this).check();

    }

    @Override
    public void onTipActionClick(int actionId) {

    }

    @Override
    public void onTipDismissed(int actionId) {

    }

    private void setChooser(int key) {
        PreferenceUtil.getInstance(this).setLastChooser(key);
        switch (key) {
            case LIBRARY:
                navigationView.setCheckedItem(R.id.nav_library);
                setCurrentFragment(LibraryFragment.newInstance());
                break;
            case EXPLORE:
                navigationView.setCheckedItem(R.id.nav_explore);
                setCurrentFragment(ExploreFragment.newInstance());
                break;
        }
    }

    private void setCurrentFragment(Fragment fragment) {
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
            if (id == R.id.nav_library) {
                new Handler().postDelayed(() -> setChooser(LIBRARY), 200);
            } else if (id == R.id.nav_explore) {
                new Handler().postDelayed(() -> setChooser(EXPLORE), 200);
            } else if (id == R.id.nav_settings) {
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

    @Override
    public void onUpdateCheckListener(String urlApp) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("New version")
                .setMessage("Update plz o_O")
                .setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "" + urlApp, Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        alertDialog.show();
    }
}