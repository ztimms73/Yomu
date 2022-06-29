package org.xtimms.yomu.loader

import android.content.Context
import androidx.loader.content.AsyncTaskLoader
import org.xtimms.yomu.misc.ListWrapper
import org.xtimms.yomu.models.MangaFavourite
import org.xtimms.yomu.storage.db.FavouritesRepository
import org.xtimms.yomu.storage.db.FavouritesSpecification

class FavouriteLoader(context: Context?, private val mSpec: FavouritesSpecification) :
    AsyncTaskLoader<ListWrapper<MangaFavourite?>>(context!!) {

    override fun loadInBackground(): ListWrapper<MangaFavourite?> {
        return try {
            ListWrapper(FavouritesRepository[context].query(mSpec))
        } catch (e: Exception) {
            e.printStackTrace()
            ListWrapper(e)
        }
    }

}