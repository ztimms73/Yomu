package org.xtimms.yomu.storage.db

import org.xtimms.yomu.storage.db.SQLiteRepository
import org.xtimms.yomu.models.MangaFavourite
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import org.xtimms.yomu.storage.db.FavouritesRepository
import org.xtimms.yomu.models.MangaHeader
import org.json.JSONArray
import org.json.JSONObject
import android.database.sqlite.SQLiteDatabase
import java.lang.Exception
import java.lang.ref.WeakReference

class FavouritesRepository private constructor(context: Context) : SQLiteRepository<MangaFavourite?>(context) {

    override fun toContentValues(t: MangaFavourite?, cv: ContentValues) {
        cv.put(projection[0], t?.id)
        cv.put(projection[1], t?.name)
        cv.put(projection[2], t?.summary)
        cv.put(projection[3], t?.genres)
        cv.put(projection[4], t?.url)
        cv.put(projection[5], t?.thumbnail)
        cv.put(projection[6], t?.provider)
        cv.put(projection[7], t?.status)
        cv.put(projection[8], t?.rating)
        cv.put(projection[9], t?.createdAt)
        cv.put(projection[10], t?.categoryId)
        cv.put(projection[11], t?.totalChapters)
        cv.put(projection[12], t?.newChapters)
    }

    override val tableName: String = "favourites"

    override fun getId(t: MangaFavourite?): Any {
        return t?.id ?: -1
    }

    override val projection: Array<String?> = arrayOf(
        "id",
        "name",
        "summary",
        "genres",
        "url",
        "thumbnail",
        "provider",
        "status",
        "rating",
        "created_at",
        "category_id",
        "total_chapters",
        "new_chapters",
        "removed"
    )

    override fun fromCursor(cursor: Cursor): MangaFavourite {
        return MangaFavourite(
            cursor.getLong(0),
            cursor.getString(1),
            cursor.getString(2),
            cursor.getString(3),
            cursor.getString(4),
            cursor.getString(5),
            cursor.getString(6),
            cursor.getInt(7),
            cursor.getShort(8),
            cursor.getLong(9),
            cursor.getInt(10),
            cursor.getInt(11),
            cursor.getInt(12)
        )
    }

    operator fun get(mangaHeader: MangaHeader): MangaFavourite? {
        try {
            storageHelper.readableDatabase.query(
                tableName,
                projection,
                "id = ?", arrayOf(mangaHeader.id.toString()),
                null,
                null,
                null,
                null
            ).use { cursor ->
                return if (cursor.moveToFirst()) {
                    fromCursor(cursor)
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            return null
        }
    }

    fun remove(manga: MangaHeader) {
        storageHelper.writableDatabase
            .delete(tableName, "id=?", arrayOf(manga.id.toString()))
    }

    fun dumps(laterThen: Long): JSONArray? {
        var cursor: Cursor? = null
        return try {
            val dump = JSONArray()
            cursor = storageHelper.readableDatabase.query(
                tableName, arrayOf(
                    "id",
                    "name",
                    "summary",
                    "genres",
                    "url",
                    "thumbnail",
                    "provider",
                    "status",
                    "createdAt",
                    "categoryId",
                    "totalChapters",
                    "newChapters",
                    "rating"
                ), "timestamp > ?", arrayOf(laterThen.toString()), null, null, null
            )
            if (cursor.moveToFirst()) {
                do {
                    val jobj = JSONObject()
                    val manga = JSONObject()
                    manga.put("id", cursor.getInt(0))
                    manga.put("name", cursor.getString(1))
                    manga.put("summary", cursor.getString(2))
                    manga.put("genres", cursor.getString(3))
                    manga.put("url", cursor.getString(4))
                    manga.put("thumbnail", cursor.getString(5))
                    manga.put("provider", cursor.getInt(6))
                    manga.put("status", cursor.getInt(7))
                    manga.put("rating", cursor.getInt(12))
                    jobj.put("manga", manga)
                    jobj.put("createdAt", cursor.getLong(8))
                    jobj.put("categoryId", cursor.getInt(9))
                    jobj.put("totalChapters", cursor.getInt(10))
                    jobj.put("newChapters", cursor.getInt(11))
                    dump.put(jobj)
                } while (cursor.moveToNext())
            }
            dump
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            cursor?.close()
        }
    }

    fun inject(jsonArray: JSONArray): Boolean {
        val database: SQLiteDatabase = storageHelper.writableDatabase
        return try {
            val len = jsonArray.length()
            database.beginTransaction()
            for (i in 0 until len) {
                val jobj = jsonArray.getJSONObject(i)
                val manga = jobj.getJSONObject("manga")
                val cv = ContentValues()
                val id = manga.getInt("id")
                cv.put("id", id)
                cv.put("name", manga.getString("name"))
                cv.put("summary", manga.getString("summary"))
                cv.put("genres", manga.getString("genres"))
                cv.put("url", manga.getString("url"))
                cv.put("thumbnail", manga.getString("thumbnail"))
                cv.put("provider", manga.getLong("provider"))
                cv.put("status", manga.getLong("status"))
                cv.put("rating", manga.getInt("rating"))
                cv.put("createdAt", jobj.getInt("createdAt"))
                cv.put("categoryId", jobj.getInt("categoryId"))
                cv.put("totalChapters", jobj.getInt("totalChapters"))
                cv.put("newChapters", jobj.getInt("newChapters"))
                if (database.update(tableName, cv, "id=?", arrayOf(id.toString())) <= 0) {
                    database.insertOrThrow(tableName, null, cv)
                }
            }
            database.setTransactionSuccessful()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            database.endTransaction()
        }
    }

    companion object {
        private var sInstanceRef: WeakReference<FavouritesRepository>? = null

        operator fun get(context: Context): FavouritesRepository {
            var instance: FavouritesRepository? = null
            if (sInstanceRef != null) {
                instance = sInstanceRef!!.get()
            }
            if (instance == null) {
                instance = FavouritesRepository(context)
                sInstanceRef = WeakReference(instance)
            }
            return instance
        }
    }
}