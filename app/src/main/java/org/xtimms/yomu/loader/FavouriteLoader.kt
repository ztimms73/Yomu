package org.xtimms.yomu.loader;

import android.content.Context;

import androidx.loader.content.AsyncTaskLoader;

import org.xtimms.yomu.misc.ListWrapper;
import org.xtimms.yomu.models.MangaFavourite;
import org.xtimms.yomu.storage.db.FavouritesRepository;
import org.xtimms.yomu.storage.db.FavouritesSpecification;

public final class FavouriteLoader extends AsyncTaskLoader<ListWrapper<MangaFavourite>> {

    private final FavouritesSpecification mSpec;

    public FavouriteLoader(Context context, FavouritesSpecification specification) {
        super(context);
        mSpec = specification;
    }

    @Override
    public ListWrapper<MangaFavourite> loadInBackground() {
        try {
            return new ListWrapper<>(FavouritesRepository.get(getContext()).query(mSpec));
        } catch (Exception e) {
            e.printStackTrace();
            return new ListWrapper<>(e);
        }
    }
}