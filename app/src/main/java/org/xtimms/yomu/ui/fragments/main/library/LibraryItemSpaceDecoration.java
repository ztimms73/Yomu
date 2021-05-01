package org.xtimms.yomu.ui.fragments.main.library;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.xtimms.yomu.adapter.library.LibraryItemType;

public class LibraryItemSpaceDecoration extends RecyclerView.ItemDecoration {

    private final int mSpacing;
    private final RecyclerView.Adapter mAdapter;

    public LibraryItemSpaceDecoration(int spacing, RecyclerView.Adapter adapter, int maxSpanSize) {
        mSpacing = spacing;
        mAdapter = adapter;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, RecyclerView parent, @NonNull RecyclerView.State state) {
        final int position = parent.getChildAdapterPosition(view);
        final int itemType = mAdapter.getItemViewType(position);
        switch (itemType) {
            case LibraryItemType.TYPE_RECENT:
            case LibraryItemType.TYPE_TIP:
            case LibraryItemType.TYPE_ITEM_DEFAULT:
            case LibraryItemType.TYPE_ITEM_SMALL:
                outRect.left = mSpacing;
                outRect.right = mSpacing;
                outRect.bottom = mSpacing;
                outRect.top = mSpacing;
        }
    }
}
