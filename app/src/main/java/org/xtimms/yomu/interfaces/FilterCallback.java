package org.xtimms.yomu.interfaces;

import org.xtimms.yomu.models.MangaGenre;
import org.xtimms.yomu.models.MangaType;

public interface FilterCallback {

    void setFilter(int sort, int additionalSort, MangaGenre[] genres, MangaType[] types);
}
