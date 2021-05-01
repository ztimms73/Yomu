package org.xtimms.yomu.misc;

import android.content.Context;
import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public abstract class DataViewHolder<D> extends RecyclerView.ViewHolder {

    @Nullable
    private D mData;

    public DataViewHolder(View itemView) {
        super(itemView);
    }

    @CallSuper
    public void bind(D d) {
        mData = d;
    }

    @CallSuper
    public void recycle() {
        mData = null;
    }

    @Nullable
    protected final D getData() {
        return mData;
    }

    public final Context getContext() {
        return itemView.getContext();
    }
}