package org.xtimms.yomu.models;

import android.content.Context;

import androidx.annotation.NonNull;

import org.xtimms.yomu.R;

public class Category {

    public final int id;
    public final String name;
    public final long createdAt;

    public Category(int id, String name, long createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    public Category(String name, long createdAt) {
        this.id = name.hashCode();
        this.name = name;
        this.createdAt = createdAt;
    }

    @NonNull
    public static Category createDefault(Context context) {
        return new Category(context.getString(R.string.favourites), System.currentTimeMillis());
    }

}