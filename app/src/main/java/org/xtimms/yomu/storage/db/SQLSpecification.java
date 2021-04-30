package org.xtimms.yomu.storage.db;

import androidx.annotation.Nullable;

public interface SQLSpecification {

    @Nullable
    String getSelection();
    @Nullable
    String[] getSelectionArgs();
    @Nullable
    String getOrderBy();
    @Nullable
    String getLimit();
}
