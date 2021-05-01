package org.xtimms.yomu.ui.fragments.main.library;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.xtimms.yomu.models.Category;
import org.xtimms.yomu.models.MangaFavourite;
import org.xtimms.yomu.models.MangaHeader;
import org.xtimms.yomu.models.MangaHistory;
import org.xtimms.yomu.models.SavedManga;
import org.xtimms.yomu.models.UserTip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LibraryContent {

    static final int SECTION_HISTORY = 2;
    static final int SECTION_SAVED = 3;

    @Nullable
    MangaHistory recent;

    @NonNull
    final ArrayList<UserTip> tips;

    @NonNull
    final ArrayList<MangaHistory> history;

    @NonNull
    final ArrayList<SavedManga> savedManga;

    @NonNull
    final HashMap<Category, List<MangaFavourite>> favourites;

    @NonNull
    final ArrayList<MangaHeader> recommended;

    LibraryContent() {
        tips = new ArrayList<>(4);
        history = new ArrayList<>();
        savedManga = new ArrayList<>();
        favourites = new HashMap<>();
        recommended = new ArrayList<>();
        recent = null;
    }

    public boolean isEmpty() {
        return recent == null && tips.isEmpty() && history.isEmpty() && savedManga.isEmpty() && favourites.isEmpty() && recommended.isEmpty();
    }

}
