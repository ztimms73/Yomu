package org.xtimms.yomu.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import java.util.Objects;

public final class MangaGenre implements Parcelable {

    @StringRes
    public final int nameId;
    @NonNull
    public final String value;

    public MangaGenre(int nameId, @NonNull String value) {
        this.nameId = nameId;
        this.value = value;
    }

    private MangaGenre(Parcel in) {
        nameId = in.readInt();
        value = Objects.requireNonNull(in.readString());
    }

    public static final Creator<MangaGenre> CREATOR = new Creator<MangaGenre>() {
        @Override
        public MangaGenre createFromParcel(Parcel in) {
            return new MangaGenre(in);
        }

        @Override
        public MangaGenre[] newArray(int size) {
            return new MangaGenre[size];
        }
    };

    @NonNull
    @Override
    public String toString() {
        return value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(nameId);
        parcel.writeString(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MangaGenre that = (MangaGenre) o;

        return nameId == that.nameId && value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return 31 * nameId + value.hashCode();
    }

    @NonNull
    public static String joinNames(@NonNull Context context, @NonNull MangaGenre[] genres, @NonNull String delimiter) {
        final StringBuilder builder = new StringBuilder();
        boolean nonFirst = false;
        for (MangaGenre o: genres) {
            if (nonFirst) {
                builder.append(delimiter);
            } else {
                nonFirst = true;
            }
            builder.append(context.getString(o.nameId));
        }
        return builder.toString();
    }

    public static int indexOf(MangaGenre[] array, @NonNull String value) {
        for (int i = 0; i < array.length; i++) {
            if (value.equalsIgnoreCase(array[i].value)) {
                return i;
            }
        }
        return -1;
    }
}
