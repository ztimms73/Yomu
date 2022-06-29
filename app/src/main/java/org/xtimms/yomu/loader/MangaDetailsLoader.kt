package org.xtimms.yomu.loader;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.loader.content.AsyncTaskLoader;

import org.xtimms.yomu.misc.ObjectWrapper;
import org.xtimms.yomu.models.MangaDetails;
import org.xtimms.yomu.models.MangaHeader;
import org.xtimms.yomu.source.MangaProvider;

public final class MangaDetailsLoader extends AsyncTaskLoader<ObjectWrapper<MangaDetails>> {

    private final MangaHeader mManga;

    public MangaDetailsLoader(Context context, MangaHeader mangaHeader) {
        super(context);
        mManga = mangaHeader;
    }

    @Override
    @NonNull
    public ObjectWrapper<MangaDetails> loadInBackground() {
        try {
            final MangaProvider provider = MangaProvider.get(getContext(), mManga.provider);
            final MangaDetails details = provider.getDetails(mManga);
            return new ObjectWrapper<>(details);
        } catch (Exception e) {
            e.printStackTrace();
            return new ObjectWrapper<>(e);
        }
    }
}
