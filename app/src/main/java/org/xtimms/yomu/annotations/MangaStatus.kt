package org.xtimms.yomu.annotations

import androidx.annotation.IntDef

@Retention(AnnotationRetention.SOURCE)
@IntDef(
    MangaStatus.STATUS_UNKNOWN,
    MangaStatus.STATUS_COMPLETED,
    MangaStatus.STATUS_ONGOING,
    MangaStatus.STATUS_LICENSED
)
annotation class MangaStatus {
    companion object {
        const val STATUS_UNKNOWN = 0
        const val STATUS_COMPLETED = 1
        const val STATUS_ONGOING = 2
        const val STATUS_LICENSED = 3
    }
}