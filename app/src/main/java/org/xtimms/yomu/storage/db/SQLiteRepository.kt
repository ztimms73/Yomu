package org.xtimms.yomu.storage.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

abstract class SQLiteRepository<T> protected constructor(context: Context?) : Repository<T> {

    protected val storageHelper: StorageHelper

    override fun add(t: T): Boolean {
        return try {
            val cv = ContentValues(projection.size)
            toContentValues(t, cv)
            storageHelper.writableDatabase
                .insert(tableName, null, cv) >= 0
        } catch (e: Exception) {
            false
        }
    }

    override fun remove(t: T): Boolean {
        return storageHelper.writableDatabase
            .delete(tableName, "id=?", arrayOf(getId(t).toString())) >= 0
    }

    open fun remove(ids: LongArray?) {}
    override fun update(t: T): Boolean {
        return try {
            val cv = ContentValues(projection.size)
            toContentValues(t, cv)
            storageHelper.writableDatabase.update(
                tableName, cv,
                "id=?", arrayOf(getId(t).toString())
            ) > 0
        } catch (e: Exception) {
            false
        }
    }

    fun addOrUpdate(t: T) {
        val cv = ContentValues(projection.size)
        toContentValues(t, cv)
        val database = storageHelper.writableDatabase
        try {
            if (database.insert(tableName, null, cv) >= 0) {
                return
            }
        } catch (ignored: Exception) {
        }
        try {
            database.update(tableName, cv, "id=?", arrayOf(getId(t).toString()))
        } catch (ignored: Exception) {
        }
    }

    fun updateOrAdd(t: T) {
        val cv = ContentValues(projection.size)
        toContentValues(t, cv)
        val database = storageHelper.writableDatabase
        try {
            if (database.update(tableName, cv, "id=?", arrayOf(getId(t).toString())) > 0) {
                return
            }
        } catch (ignored: Exception) {
        }
        try {
            database.insert(tableName, null, cv)
        } catch (ignored: Exception) {
        }
    }

    override fun clear() {
        storageHelper.writableDatabase.delete(tableName, null, null)
    }

    override fun contains(t: T): Boolean {
        try {
            storageHelper.readableDatabase.rawQuery(
                "SELECT * FROM $tableName WHERE id = ?",
                arrayOf(getId(t).toString())
            ).use { cursor -> return cursor.count > 0 }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    override fun query(specification: SQLSpecification): ArrayList<T>? {
        try {
            storageHelper.readableDatabase.query(
                tableName,
                projection,
                specification.selection,
                specification.selectionArgs,
                null,
                null,
                specification.orderBy,
                specification.limit
            ).use { cursor ->
                val list = ArrayList<T>()
                if (cursor.moveToFirst()) {
                    do {
                        list.add(fromCursor(cursor))
                    } while (cursor.moveToNext())
                }
                return list
            }
        } catch (e: Exception) {
            return null
        }
    }

    protected fun findById(id: Any): T? {
        try {
            storageHelper.readableDatabase.query(
                tableName,
                projection,
                "id = ?", arrayOf(id.toString()),
                null,
                null,
                null
            ).use { cursor ->
                return if (cursor.moveToFirst()) {
                    fromCursor(cursor)
                } else null
            }
        } catch (e: Exception) {
            return null
        }
    }

    protected abstract fun toContentValues(t: T, cv: ContentValues)
    protected abstract val tableName: String
    protected abstract fun getId(t: T): Any
    protected abstract val projection: Array<String?>
    protected abstract fun fromCursor(cursor: Cursor): T

    init {
        storageHelper = StorageHelper(context)
    }
}