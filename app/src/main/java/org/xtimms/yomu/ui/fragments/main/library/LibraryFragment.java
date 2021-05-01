package org.xtimms.yomu.ui.fragments.main.library;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.kabouzeid.appthemehelper.ThemeStore;

import org.xtimms.yomu.R;
import org.xtimms.yomu.adapter.library.LibraryAdapter;
import org.xtimms.yomu.listeners.OnTipsActionListener;
import org.xtimms.yomu.misc.Dismissible;
import org.xtimms.yomu.ui.fragments.main.AbsMainActivityFragment;
import org.xtimms.yomu.util.PreferenceUtil;
import org.xtimms.yomu.util.ResourceUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class LibraryFragment extends AbsMainActivityFragment implements SharedPreferences.OnSharedPreferenceChangeListener, LoaderManager.LoaderCallbacks<LibraryContent> {

    private Unbinder unbinder;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private LibraryAdapter mAdapter;
    private int mColumnCount;
    private boolean mWasPaused;

    public static LibraryFragment newInstance() {
        return new LibraryFragment();
    }

    public LibraryFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mColumnCount = 12;
        mWasPaused = false;
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
        unbinder.unbind();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        PreferenceUtil.getInstance(getActivity()).registerOnSharedPreferenceChangedListener(this);
        getMainActivity().setStatusbarColorAuto();
        getMainActivity().setNavigationbarColorAuto();
        getMainActivity().setTaskDescriptionColorAuto();

        recyclerView.setHasFixedSize(true);
        new ItemTouchHelper(new DismissCallback()).attachToRecyclerView(recyclerView);
        recyclerView.setClipToPadding(false);
        recyclerView.setPadding(
                recyclerView.getPaddingLeft(),
                recyclerView.getPaddingTop(),
                recyclerView.getPaddingRight(),
                recyclerView.getPaddingBottom()
        );

        setUpToolbar();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (ResourceUtil.isTablet(getResources().getConfiguration())) {
            mColumnCount = 24;
        }
        mAdapter = new LibraryAdapter((OnTipsActionListener) getMainActivity());
        GridLayoutManager layoutManager = new GridLayoutManager(getMainActivity(), mColumnCount);
        layoutManager.setSpanSizeLookup(new LibrarySpanSizeLookup(mAdapter, mColumnCount));
        recyclerView.addItemDecoration(new LibraryItemSpaceDecoration(ResourceUtil.dpToPx(getResources(), 4), mAdapter, mColumnCount));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this).forceLoad();
        mWasPaused = false;
    }

    private void setUpToolbar() {
        int primaryColor = ThemeStore.primaryColor(getActivity());
        appbar.setBackgroundColor(primaryColor);
        toolbar.setBackgroundColor(primaryColor);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        getActivity().setTitle(R.string.library);
        getMainActivity().setSupportActionBar(toolbar);
    }

    @Override
    public void onPause() {
        mWasPaused = true;
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mWasPaused) {
            getLoaderManager().restartLoader(0, null, this).forceLoad();
            mWasPaused = false;
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    @NonNull
    @Override
    public Loader<LibraryContent> onCreateLoader(int id, @Nullable Bundle args) {
        return new LibraryLoader(getActivity(), mColumnCount);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<LibraryContent> loader, LibraryContent data) {
        LibraryUpdater.update(mAdapter, data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<LibraryContent> loader) {

    }

    private static class DismissCallback extends ItemTouchHelper.SimpleCallback {

        DismissCallback() {
            super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        }

        @Override
        public int getSwipeDirs(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            return viewHolder instanceof LibraryAdapter.TipHolder && ((LibraryAdapter.TipHolder) viewHolder).isDismissible() ?
                    super.getSwipeDirs(recyclerView, viewHolder) : 0;
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            if (viewHolder instanceof Dismissible) {
                ((Dismissible) viewHolder).dismiss();
            }
        }
    }

}
