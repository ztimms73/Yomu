package org.xtimms.yomu.interfaces

import android.widget.Checkable

interface ExtraCheckable : Checkable {

    fun isCheckedAnimated(checked: Boolean)

}