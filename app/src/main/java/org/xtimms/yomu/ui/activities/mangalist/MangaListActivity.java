package org.xtimms.yomu.ui.activities.mangalist;

import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.kabouzeid.appthemehelper.ThemeStore;

import org.xtimms.yomu.R;
import org.xtimms.yomu.adapter.InfiniteAdapter;
import org.xtimms.yomu.adapter.mangalist.MangaListAdapter;
import org.xtimms.yomu.misc.ListModeHelper;
import org.xtimms.yomu.misc.ListWrapper;
import org.xtimms.yomu.misc.PagedList;
import org.xtimms.yomu.misc.ThumbSize;
import org.xtimms.yomu.models.MangaGenre;
import org.xtimms.yomu.models.MangaHeader;
import org.xtimms.yomu.source.MangaProvider;
import org.xtimms.yomu.ui.base.AbsBaseActivity;
import org.xtimms.yomu.util.ErrorUtil;
import org.xtimms.yomu.util.KaomojiUtil;
import org.xtimms.yomu.util.LayoutUtil;
import org.xtimms.yomu.views.InfiniteRecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class MangaListActivity extends AbsBaseActivity implements LoaderManager.LoaderCallbacks<ListWrapper<MangaHeader>>,
        View.OnClickListener, SearchView.OnQueryTextListener, InfiniteRecyclerView.OnLoadMoreListener, ListModeHelper.OnListModeListener, InfiniteAdapter.OnLoadMoreListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.fabFilter)
    FloatingActionButton fab;
    @BindView(R.id.recyclerView)
    InfiniteRecyclerView recyclerView;
    @BindView(R.id.stub_error)
    View error;

    private MenuItem mMenuItemSearch;
    private TextView mTextViewError;
    private TextView mTextViewFace;

    private final PagedList<MangaHeader> mDataset = new PagedList<>();
    private MangaListAdapter mAdapter;
    private MangaProvider mProvider;
    private ListModeHelper mListModeHelper;
    private final MangaQueryArguments mArguments = new MangaQueryArguments();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDrawUnderStatusbar();
        ButterKnife.bind(this);

        setStatusbarColorAuto();
        setNavigationbarColorAuto();
        setTaskDescriptionColorAuto();
        setUpToolbar();
        setUpViews();

        load();
    }

    private void setUpViews() {
        int accentColor = ThemeStore.accentColor(this);
        mAdapter = new MangaListAdapter(mDataset, recyclerView);
        fab.setOnClickListener(this);
        mAdapter.notifyDataSetChanged();
        mAdapter.setOnLoadMoreListener(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.scheduleLayoutAnimation();
        recyclerView.setOnLoadMoreListener(this);

        progressBar.getIndeterminateDrawable().setColorFilter(accentColor, android.graphics.PorterDuff.Mode.MULTIPLY);

        mListModeHelper = new ListModeHelper(this, this);
        mListModeHelper.applyCurrent();
        mListModeHelper.enable();

        final String cname = getIntent().getStringExtra("provider.cname");
        assert cname != null;
        mProvider = MangaProvider.get(this, cname);
        setTitle(mProvider.getName());
        if (mProvider.getAvailableGenres().length == 0 && mProvider.getAvailableTypes().length == 0 && mProvider.getAvailableSortOrders().length == 0 && mProvider.getAvailableAdditionalSortOrders().length == 0) {
            fab.hide();
        }

        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected View createContentView() {
        View contentView = getLayoutInflater().inflate(R.layout.activity_manga_list, null);
        return contentView;
    }

    private void setUpToolbar() {
        toolbar.setBackgroundColor(ThemeStore.primaryColor(this));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onDestroy() {
        mListModeHelper.disable();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mListModeHelper.applyCurrent();
    }

    @Override
    public Loader<ListWrapper<MangaHeader>> onCreateLoader(int i, Bundle bundle) {
        final MangaQueryArguments queryArgs = MangaQueryArguments.from(bundle);

        toolbar.setSubtitle(!TextUtils.isEmpty(queryArgs.query)
                ? queryArgs.query
                : queryArgs.genres.length != 0
                ? MangaGenre.joinNames(this, queryArgs.genres, ", ")
                : null);

        return new MangaListLoader(this, mProvider, queryArgs);
    }

    @Override
    public void onLoadFinished(Loader<ListWrapper<MangaHeader>> loader, ListWrapper<MangaHeader> result) {
        progressBar.setVisibility(View.GONE);
        if (result.isSuccess()) {
            final ArrayList<MangaHeader> list = result.get();
            int firstPos = mDataset.size();
            mDataset.addAll(list);
            if (firstPos == 0) {
                mAdapter.notifyDataSetChanged();
            } else {
                mAdapter.notifyItemRangeInserted(firstPos, list.size());
            }
            recyclerView.onLoadingFinished(!list.isEmpty());
        } else {
            setError(result.getError());
            recyclerView.onLoadingFinished(false);
        }
    }

    @Override
    public void onLoaderReset(Loader<ListWrapper<MangaHeader>> loader) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_retry:
                progressBar.setVisibility(View.VISIBLE);
            case com.google.android.material.R.id.snackbar_action:
                mArguments.page++;
                load();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mMenuItemSearch.collapseActionView();
        if (TextUtils.equals(query, mArguments.query)) {
            return true;
        }
        mArguments.query = query;
        loadFirstPage();
        return true;
    }

    /**
     * Сбрасываем на первую страницу
     */
    private void loadFirstPage() {
        mArguments.page = 0;
        progressBar.setVisibility(View.VISIBLE);
        mDataset.clear();
        mAdapter.notifyDataSetChanged();
        load();
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onLoadMore() {
        mArguments.page++;
        load();
        return true;
    }

    private void setError(Throwable e) {
        if (mDataset.isEmpty()) {
            if (error instanceof ViewStub) {
                error = ((ViewStub) error).inflate();
                mTextViewError = error.findViewById(R.id.textView_error);
                mTextViewFace = error.findViewById(R.id.text_face);
                error.findViewById(R.id.button_retry).setOnClickListener(this);
            }
            mTextViewFace.setText(KaomojiUtil.getRandomErrorFace());
            mTextViewError.setText(ErrorUtil.getErrorMessage(this, e));
            error.setVisibility(View.VISIBLE);
        } else {
            Snackbar.make(recyclerView, ErrorUtil.getErrorMessage(e), Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.retry, this)
                    .show();
        }
    }

    /**
     * Лоадер не перезагрузится если
     * Bundle тот же самый с темиже значениями (вроде)
     */
    private void load() {
        recyclerView.onLoadingStarted();
        error.setVisibility(View.GONE);
		/*
	  loader's id
	 */
        int LOADER_ID = 234;
        Loader<ListWrapper<MangaHeader>> loader = getSupportLoaderManager().getLoader(LOADER_ID);
        if (loader == null) {
            loader = getSupportLoaderManager().initLoader(LOADER_ID, mArguments.toBundle(), this);
        } else {
            loader = getSupportLoaderManager().restartLoader(LOADER_ID, mArguments.toBundle(), this);
        }
        loader.forceLoad();
        mAdapter.setLoaded();
    }

    public void updateLayout(boolean grid, int spanCount, ThumbSize thumbSize) {
        GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
        assert layoutManager != null;
        int position = layoutManager.findFirstCompletelyVisibleItemPosition();
        layoutManager.setSpanCount(spanCount);
//		layoutManager.setSpanSizeLookup(new MangaListAdapter.AutoSpanSizeLookup(spanCount));
        mAdapter.setThumbnailsSize(thumbSize);
        if (mAdapter.setGrid(grid)) {
            recyclerView.setAdapter(mAdapter);
        }
        recyclerView.scrollToPosition(position);
    }

    @Override
    public void onListModeChanged(boolean grid, int sizeMode) {
        int spans;
        ThumbSize thumbSize;
        switch (sizeMode) {
            case 0:
                spans = LayoutUtil.getOptimalColumnsCount(getResources(), thumbSize = ThumbSize.THUMB_SIZE_SMALL);
                break;
            case 1:
                spans = LayoutUtil.getOptimalColumnsCount(getResources(), thumbSize = ThumbSize.THUMB_SIZE_MEDIUM);
                break;
            case 2:
                spans = LayoutUtil.getOptimalColumnsCount(getResources(), thumbSize = ThumbSize.THUMB_SIZE_LARGE);
                break;
            default:
                return;
        }
        updateLayout(grid, spans, thumbSize);
    }

}
