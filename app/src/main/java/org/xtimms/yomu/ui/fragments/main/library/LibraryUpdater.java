package org.xtimms.yomu.ui.fragments.main.library;

import org.xtimms.yomu.R;
import org.xtimms.yomu.adapter.library.LibraryAdapter;
import org.xtimms.yomu.models.Category;
import org.xtimms.yomu.models.ListHeader;
import org.xtimms.yomu.models.MangaFavourite;
import org.xtimms.yomu.models.MangaHeader;
import org.xtimms.yomu.models.MangaHistory;
import org.xtimms.yomu.models.SavedManga;

import java.util.ArrayList;
import java.util.List;

public class LibraryUpdater {

    public static void update(LibraryAdapter adapter, LibraryContent content) {
        ArrayList<Object> dataset = new ArrayList<>(content.tips);
        if (content.recent != null || !content.history.isEmpty()) {
            dataset.add(new ListHeader(R.string.action_history, LibraryContent.SECTION_HISTORY));
            if (content.recent != null) {
                dataset.add(content.recent);
            }
            for (MangaHistory o : content.history) {
                dataset.add(MangaHeader.from(o));
            }
        }
        for (Category category : content.favourites.keySet()) {
            List<MangaFavourite> favourites = content.favourites.get(category);
            if (favourites != null && !favourites.isEmpty()) {
                dataset.add(new ListHeader(category.getName(), category.getId()));
                dataset.addAll(favourites);
            }
        }
        if (!content.savedManga.isEmpty()) {
            dataset.add(new ListHeader(R.string.saved_manga, LibraryContent.SECTION_SAVED));
            for (SavedManga o : content.savedManga) {
                dataset.add(SavedManga.from(o));
            }
        }
        if (!content.recommended.isEmpty()) {
            dataset.add(new ListHeader(R.string.recommendations, null /*TODO*/));
            dataset.addAll(content.recommended);
        }
        dataset.trimToSize();
        adapter.updateData(dataset);
    }

}
