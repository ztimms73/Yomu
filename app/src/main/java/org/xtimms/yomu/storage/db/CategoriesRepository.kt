package org.xtimms.yomu.storage.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import org.xtimms.yomu.models.Category
import java.lang.ref.WeakReference

class CategoriesRepository private constructor(context: Context) : SQLiteRepository<Category?>(context) {

    override fun fromCursor(cursor: Cursor): Category {
        return Category(
            cursor.getInt(0),
            cursor.getString(1),
            cursor.getLong(2)
        )
    }

    override fun toContentValues(t: Category?, cv: ContentValues) {
        cv.put(projection[0], t?.id)
        cv.put(projection[1], t?.name)
        cv.put(projection[2], t?.createdAt)
    }

    override val tableName: String = "categories"

    override fun getId(t: Category?): Any {
        return t?.id ?: -1
    }

    override val projection: Array<String?> = arrayOf(
        "id",
        "name",
        "created_at"
    )

    companion object {

        private var sInstanceRef: WeakReference<CategoriesRepository>? = null

        operator fun get(context: Context): CategoriesRepository {
            var instance: CategoriesRepository? = null
            if (sInstanceRef != null) {
                instance = sInstanceRef!!.get()
            }
            if (instance == null) {
                instance = CategoriesRepository(context)
                sInstanceRef = WeakReference(instance)
            }
            return instance
        }
    }

}