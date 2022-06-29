package org.xtimms.yomu.loader

import android.content.Context
import androidx.loader.content.AsyncTaskLoader
import org.xtimms.yomu.misc.ObjectWrapper
import org.xtimms.yomu.models.MangaHeader
import org.xtimms.yomu.models.MangaDetails
import org.xtimms.yomu.source.MangaProvider
import java.lang.Exception

class MangaDetailsLoader(context: Context?, private val mManga: MangaHeader) :
    AsyncTaskLoader<ObjectWrapper<MangaDetails>>(context!!) {

    override fun loadInBackground(): ObjectWrapper<MangaDetails> {
        return try {
            val provider = MangaProvider.get(context, mManga.provider)
            val details = provider.getDetails(mManga)
            ObjectWrapper(details)
        } catch (e: Exception) {
            e.printStackTrace()
            ObjectWrapper(e)
        }
    }

}