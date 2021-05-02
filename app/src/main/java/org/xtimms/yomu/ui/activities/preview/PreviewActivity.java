package org.xtimms.yomu.ui.activities.preview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.BroadcastReceiver;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.tabs.TabLayout;
import com.kabouzeid.appthemehelper.ThemeStore;
import com.kabouzeid.appthemehelper.util.TabLayoutUtil;
import com.kabouzeid.appthemehelper.util.ToolbarContentTintHelper;

import androidx.appcompat.widget.Toolbar;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import org.xtimms.yomu.R;
import org.xtimms.yomu.adapter.PagesAdapter;
import org.xtimms.yomu.loader.MangaDetailsLoader;
import org.xtimms.yomu.misc.ObjectWrapper;
import org.xtimms.yomu.models.MangaDetails;
import org.xtimms.yomu.models.MangaFavourite;
import org.xtimms.yomu.models.MangaHeader;
import org.xtimms.yomu.models.MangaHistory;
import org.xtimms.yomu.storage.db.FavouritesRepository;
import org.xtimms.yomu.storage.db.HistoryRepository;
import org.xtimms.yomu.ui.activities.preview.chapters.ChaptersPage;
import org.xtimms.yomu.ui.activities.preview.details.DetailsPage;
import org.xtimms.yomu.ui.base.AbsBaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class PreviewActivity extends AbsBaseActivity implements LoaderManager.LoaderCallbacks<ObjectWrapper<MangaDetails>>,
        ViewPager.OnPageChangeListener, SearchView.OnQueryTextListener {

    public static final String ACTION_PREVIEW = "org.xtimms.yomu.ACTION_PREVIEW";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.pager)
    ViewPager pager;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    //tabs
    private DetailsPage mDetailsPage;
    private ChaptersPage mChaptersPage;
    //data
    private MangaHeader mMangaHeader;
    @Nullable
    private MangaDetails mMangaDetails;
    private HistoryRepository mHistory;
    private FavouritesRepository mFavourites;
    private BroadcastReceiver mDownloadsReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDrawUnderStatusbar();
        ButterKnife.bind(this);

        setStatusbarColorAuto();
        setNavigationbarColorAuto();
        setTaskDescriptionColorAuto();
        setUpToolbar();
        setUpTabLayout();
        setUpViews();
        setUpPages();
    }

    private void setUpTabLayout() {
        int primaryColor = ThemeStore.primaryColor(this);
        int normalColor = ToolbarContentTintHelper.toolbarSubtitleColor(this, primaryColor);
        int selectedColor = ToolbarContentTintHelper.toolbarTitleColor(this, primaryColor);
        TabLayoutUtil.setTabIconColors(tabLayout, normalColor, selectedColor);
        tabLayout.setBackgroundDrawable(new ColorDrawable(primaryColor));
        tabLayout.setTabTextColors(normalColor, selectedColor);
        tabLayout.setSelectedTabIndicatorColor(ThemeStore.accentColor(this));
    }

    private void setUpPages() {

        mMangaHeader = getIntent().getParcelableExtra("manga");
        mMangaDetails = null;
        assert mMangaHeader != null;

        mFavourites = FavouritesRepository.get(this);
        mHistory = HistoryRepository.get(this);

        mDetailsPage.updateContent(mMangaHeader, mMangaDetails);

        Bundle args = new Bundle(1);
        args.putParcelable("manga", mMangaHeader);
        getSupportLoaderManager().initLoader(0, args, this).forceLoad();
    }

    private void setUpViews() {

        int accentColor = ThemeStore.accentColor(this);

        final PagesAdapter adapter = new PagesAdapter(
                mDetailsPage = new DetailsPage(pager),
                mChaptersPage = new ChaptersPage(pager)
        );

        progressBar.getIndeterminateDrawable().setColorFilter(accentColor, PorterDuff.Mode.MULTIPLY);

        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);
        pager.addOnPageChangeListener(this);
    }

    @Override
    protected View createContentView() {
        View contentView = getLayoutInflater().inflate(R.layout.activity_preview, null);
        return contentView;
    }

    private void setUpToolbar() {
        toolbar.setBackgroundColor(ThemeStore.primaryColor(this));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_preview, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final int page = pager.getCurrentItem();
        menu.setGroupVisible(R.id.group_details, page == 0);
        return super.onPrepareOptionsMenu(menu);
    }

    private void onPrepareBottomBarMenu(Menu menu) {
        final MenuItem item = menu.findItem(R.id.action_reverse);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        invalidateOptionsMenu();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    @NonNull
    @Override
    public Loader<ObjectWrapper<MangaDetails>> onCreateLoader(int id, @Nullable Bundle args) {
        return new MangaDetailsLoader(this, args.getParcelable("manga"));
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ObjectWrapper<MangaDetails>> loader, ObjectWrapper<MangaDetails> data) {
        progressBar.setVisibility(View.GONE);
        if (data.isSuccess()) {
            mMangaDetails = data.get();
            assert mMangaDetails != null;
            toolbar.setTitle(mMangaDetails.name);
            toolbar.setSubtitle(mMangaDetails.summary);
            final MangaHistory history = mHistory.find(mMangaHeader);
            mDetailsPage.updateContent(mMangaHeader, mMangaDetails);
            mChaptersPage.setData(mMangaDetails.chapters, history, null);
        } else {
            mChaptersPage.setError();
            mDetailsPage.setError(data.getError());
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ObjectWrapper<MangaDetails>> loader) {

    }
}