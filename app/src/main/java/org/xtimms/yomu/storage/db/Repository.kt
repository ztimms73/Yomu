package org.xtimms.yomu.storage.db

import org.xtimms.yomu.storage.db.SQLSpecification

interface Repository<T> {

    fun add(t: T): Boolean
    fun remove(t: T): Boolean
    fun update(t: T): Boolean
    fun clear()
    operator fun contains(t: T): Boolean
    fun query(specification: SQLSpecification): List<T>?

}