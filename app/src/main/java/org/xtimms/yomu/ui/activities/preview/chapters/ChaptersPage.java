package org.xtimms.yomu.ui.activities.preview.chapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.xtimms.yomu.R;
import org.xtimms.yomu.adapter.preview.ChaptersListAdapter;
import org.xtimms.yomu.misc.PageHolder;
import org.xtimms.yomu.models.MangaChaptersList;
import org.xtimms.yomu.models.MangaHistory;
import org.xtimms.yomu.util.LayoutUtil;

public final class ChaptersPage extends PageHolder {

    private RecyclerView mRecyclerViewChapters;
    private TextView mTextViewChaptersHolder;
    @Nullable
    private ChaptersListAdapter mChaptersAdapter;

    public ChaptersPage(@NonNull ViewGroup parent) {
        super(parent, R.layout.page_manga_chapters);
    }

    @Override
    protected void onViewCreated(@NonNull View view) {
        mRecyclerViewChapters = view.findViewById(R.id.recyclerView_chapters);
        mRecyclerViewChapters.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mRecyclerViewChapters.addItemDecoration(new DividerItemDecoration(view.getContext(), LinearLayoutManager.VERTICAL));
        mTextViewChaptersHolder = view.findViewById(R.id.textView_chapters_holder);
    }

    public void setData(MangaChaptersList chapters, @Nullable MangaHistory history,
                        ChaptersListAdapter.OnChapterClickListener chapterClickListener) {
        mChaptersAdapter = new ChaptersListAdapter(getContext(), chapters, chapterClickListener);
        if (history == null) {
            mChaptersAdapter.setCurrentChapterId(0);
        } else {
            mChaptersAdapter.setCurrentChapterId(history.chapterId);
        }
        mRecyclerViewChapters.setAdapter(mChaptersAdapter);
        if (chapters.isEmpty()) {
            mTextViewChaptersHolder.setText(R.string.no_chapters_found);
            mTextViewChaptersHolder.setVisibility(View.VISIBLE);
        } else {
            mTextViewChaptersHolder.setVisibility(View.GONE);
        }
    }

    public void setError() {
        mTextViewChaptersHolder.setText(R.string.failed_to_load_chapters);
        mTextViewChaptersHolder.setVisibility(View.VISIBLE);
    }

    public void updateHistory(MangaHistory history) {
        if (mChaptersAdapter != null) {
            mChaptersAdapter.setCurrentChapterId(history.chapterId);
            mChaptersAdapter.notifyDataSetChanged();
        }
    }

    public boolean setReversed(boolean reversed) {
        if (mChaptersAdapter != null && mChaptersAdapter.isReversed() != reversed) {
            mChaptersAdapter.reverse();
            return true;
        } else {
            return false;
        }
    }

    public void setFilter(String str) {
        final int pos = mChaptersAdapter != null ? mChaptersAdapter.findByNameContains(str) : -1;
        if (pos != -1) {
            LayoutUtil.setSelectionFromTop(mRecyclerViewChapters, pos);
        }
    }

    public void update() {
        update(-1);
    }

    public void update(int index) {
        if (mChaptersAdapter != null) {
            if (index == -1) {
                mChaptersAdapter.notifyDataSetChanged();
            } else {
                mChaptersAdapter.notifyItemChanged(index);
            }
        }
    }
}