package org.xtimms.yomu.adapter.library;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({LibraryItemType.TYPE_ITEM_DEFAULT, LibraryItemType.TYPE_ITEM_SMALL, LibraryItemType.TYPE_HEADER, LibraryItemType.TYPE_TIP, LibraryItemType.TYPE_RECENT})
public @interface LibraryItemType {
    int TYPE_ITEM_DEFAULT = 0;
    int TYPE_ITEM_SMALL = 1;
    int TYPE_TIP = 2;
    int TYPE_HEADER = 3;
    int TYPE_RECENT = 4;
}