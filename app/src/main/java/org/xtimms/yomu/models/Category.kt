package org.xtimms.yomu.models

import android.content.Context
import org.xtimms.yomu.R

class Category {

    val id: Int
    val name: String
    val createdAt: Long

    constructor(id: Int, name: String, createdAt: Long) {
        this.id = id
        this.name = name
        this.createdAt = createdAt
    }

    constructor(name: String, createdAt: Long) {
        id = name.hashCode()
        this.name = name
        this.createdAt = createdAt
    }

    companion object {
        fun createDefault(context: Context): Category {
            return Category(context.getString(R.string.favourites), System.currentTimeMillis())
        }
    }
}