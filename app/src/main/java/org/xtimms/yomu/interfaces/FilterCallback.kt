package org.xtimms.yomu.interfaces

import org.xtimms.yomu.models.MangaGenre
import org.xtimms.yomu.models.MangaType

interface FilterCallback {

    fun setFilter(
        sort: Int,
        additionalSort: Int,
        genres: Array<MangaGenre?>?,
        types: Array<MangaType?>?
    )

}