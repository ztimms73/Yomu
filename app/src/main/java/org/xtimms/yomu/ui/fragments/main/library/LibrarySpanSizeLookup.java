package org.xtimms.yomu.ui.fragments.main.library;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.xtimms.yomu.adapter.library.LibraryItemType;

public class LibrarySpanSizeLookup extends GridLayoutManager.SpanSizeLookup {

    private final int mMaxSpans;
    private final RecyclerView.Adapter mAdapter;

    public LibrarySpanSizeLookup(RecyclerView.Adapter adapter, int maxSpans) {
        mAdapter = adapter;
        mMaxSpans = maxSpans;
    }

    @Override
    public int getSpanSize(int position) {
        switch (mAdapter.getItemViewType(position)) {
            case LibraryItemType.TYPE_ITEM_DEFAULT:
                return 4;
            case LibraryItemType.TYPE_ITEM_SMALL:
                return 3;
            case LibraryItemType.TYPE_TIP:
            case LibraryItemType.TYPE_RECENT:
            case LibraryItemType.TYPE_HEADER:
                return mMaxSpans;
            default:
                return 1;
        }
    }

}
