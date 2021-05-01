package org.xtimms.yomu.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class MangaPage implements Parcelable, UniqueObject {

    public final long id;
    @NonNull
    public final String url;
    public final String provider;

    public MangaPage(String url, String provider) {
        this.url = url;
        this.provider = provider;
        this.id = provider.hashCode() + url.hashCode();
    }

    public MangaPage(long id, @NonNull String url, String provider) {
        this.id = id;
        this.url = url;
        this.provider = provider;
    }

    @SuppressWarnings("ConstantConditions")
    protected MangaPage(Parcel in) {
        id = in.readLong();
        url = in.readString();
        provider = in.readString();
    }

    public static final Creator<MangaPage> CREATOR = new Creator<MangaPage>() {
        @Override
        public MangaPage createFromParcel(Parcel in) {
            return new MangaPage(in);
        }

        @Override
        public MangaPage[] newArray(int size) {
            return new MangaPage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(url);
        dest.writeString(provider);
    }

    @Override
    public long getId() {
        return id;
    }
}
