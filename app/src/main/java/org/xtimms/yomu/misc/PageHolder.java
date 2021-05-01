package org.xtimms.yomu.misc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

public abstract class PageHolder {

    @NonNull
    private final View mView;

    public PageHolder(@NonNull ViewGroup parent, @LayoutRes int layoutResId) {
        mView = onCreateView(parent, layoutResId);
        onViewCreated(mView);
    }

    @NonNull
    protected View onCreateView(@NonNull ViewGroup parent, @LayoutRes int layoutResId) {
        return LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
    }

    protected abstract void onViewCreated(@NonNull View view);

    public String getTitle() {
        return String.valueOf(mView.getTag());
    }

    @NonNull
    public final View getView() {
        return mView;
    }

    protected Context getContext() {
        return mView.getContext();
    }

    protected String getString(@StringRes int resId) {
        return mView.getContext().getString(resId);
    }
}
