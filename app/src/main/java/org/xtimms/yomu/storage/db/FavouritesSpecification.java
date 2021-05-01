package org.xtimms.yomu.storage.db;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.xtimms.yomu.models.UniqueObject;

import java.util.ArrayList;

public final class FavouritesSpecification implements SQLSpecification, UniqueObject {

    @Nullable
    private String mOrderBy = null;
    @Nullable
    private String mLimit = null;
    @Nullable
    private Integer mCategory = null;
    private boolean mOnlyNew = false;
    private boolean mRemoved = false;

    public FavouritesSpecification removed(boolean value) {
        mRemoved = value;
        return this;
    }

    public FavouritesSpecification category(int category) {
        mCategory = category;
        return this;
    }

    public FavouritesSpecification onlyWithNewChapters() {
        mOnlyNew = true;
        return this;
    }

    public FavouritesSpecification orderByDate(boolean descending) {
        mOrderBy = "created_at";
        if (descending) {
            mOrderBy += " DESC";
        }
        return this;
    }

    public FavouritesSpecification orderByName(boolean descending) {
        mOrderBy = "name";
        if (descending) {
            mOrderBy += " DESC";
        }
        return this;
    }

    public FavouritesSpecification orderByNewChapters() {
        mOrderBy = "new_chapters DESC";
        return this;
    }

    public FavouritesSpecification limit(int limit) {
        mLimit = String.valueOf(limit);
        return this;
    }

    @Override
    public String getSelection() {
        final StringBuilder builder = new StringBuilder("removed = ?");
        if (mCategory != null) {
            builder.append(" AND category_id = ?");
        }
        if (mOnlyNew) {
            builder.append(" AND new_chapters > 0");
        }
        return builder.toString();
    }

    @Override
    public String[] getSelectionArgs() {
        ArrayList<String> args = new ArrayList<>(4);
        args.add(mRemoved ? "1" : "0");
        if (mCategory != null) {
            args.add(String.valueOf(mCategory));
        }
        return args.toArray(new String[0]);
    }

    @Nullable
    @Override
    public String getOrderBy() {
        return mOrderBy;
    }

    @Nullable
    @Override
    public String getLimit() {
        return mLimit;
    }

    @NonNull
    public Bundle toBundle() {
        final Bundle bundle = new Bundle(5);
        bundle.putString("order_by", mOrderBy);
        bundle.putString("limit", mLimit);
        if (mCategory != null) {
            bundle.putInt("category", mCategory);
        }
        bundle.putBoolean("only_new", mOnlyNew);
        bundle.putBoolean("removed", mRemoved);
        return bundle;
    }

    @NonNull
    public static FavouritesSpecification from(Bundle bundle) {
        final FavouritesSpecification spec = new FavouritesSpecification();
        spec.mOrderBy = bundle.getString("order_by");
        spec.mLimit = bundle.getString("limit");
        if (bundle.containsKey("category")) {
            spec.mCategory = bundle.getInt("category", 0);
        }
        spec.mOnlyNew = bundle.getBoolean("only_new");
        spec.mRemoved = bundle.getBoolean("removed");
        return spec;
    }

    @Override
    public long getId() {
        return mCategory == null ? 0 : mCategory;
    }
}
