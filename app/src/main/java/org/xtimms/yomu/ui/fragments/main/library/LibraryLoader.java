package org.xtimms.yomu.ui.fragments.main.library;

import android.content.Context;

import androidx.loader.content.AsyncTaskLoader;

import org.xtimms.yomu.R;
import org.xtimms.yomu.misc.AppSettings;
import org.xtimms.yomu.misc.FlagsStorage;
import org.xtimms.yomu.models.Category;
import org.xtimms.yomu.models.MangaFavourite;
import org.xtimms.yomu.models.MangaHistory;
import org.xtimms.yomu.models.SavedManga;
import org.xtimms.yomu.models.UserTip;
import org.xtimms.yomu.storage.db.CategoriesRepository;
import org.xtimms.yomu.storage.db.CategoriesSpecification;
import org.xtimms.yomu.storage.db.FavouritesRepository;
import org.xtimms.yomu.storage.db.FavouritesSpecification;

import java.util.ArrayList;

public class LibraryLoader extends AsyncTaskLoader<LibraryContent> {

    private final int mColumnCount;

    LibraryLoader(Context context, int columnCount) {
        super(context);
        mColumnCount = columnCount;
    }

    @Override
    public LibraryContent loadInBackground() {
        final LibraryContent content = new LibraryContent();
        final LibrarySettings settings = AppSettings.get(getContext()).librarySettings;
        int len = mColumnCount / 3 * settings.getMaxHistoryRows();
        if (settings.isRecentEnabled()) {
            len++;
        }
        //tips
        //favourites
        if (settings.isFavouritesEnabled()) {
            final CategoriesRepository categoriesRepository = CategoriesRepository.Companion.get(getContext());
            ArrayList<Category> categories = categoriesRepository.query(new CategoriesSpecification().orderByDate(true));
            if (categories != null) {
                if (categories.isEmpty()) {
                    Category defaultCategory = Category.Companion.createDefault(getContext());
                    categories.add(defaultCategory);
                    categoriesRepository.add(defaultCategory);
                    LibrarySettings.onCategoryAdded(getContext(), defaultCategory);
                } else {
                    categories = settings.getEnabledCategories(categories);
                    final FavouritesRepository favouritesRepository = FavouritesRepository.Companion.get(getContext());
                    for (Category category : categories) {
                        len = mColumnCount / 4 * settings.getMaxFavouritesRows();
                        ArrayList<MangaFavourite> favourites = favouritesRepository.query(new FavouritesSpecification().orderByDate(true).category(category.getId()).limit(len));
                        if (favourites != null && !favourites.isEmpty()) {
                            content.favourites.put(category, favourites);
                        }
                    }
                }
            }
        }
        //TODO
        final FlagsStorage flagsStorage = FlagsStorage.get(getContext());
        if (content.isEmpty()) {
            content.tips.add(new UserTip(
                    getContext().getString(R.string.library_is_empty),
                    getContext().getString(R.string.nothing_here_yet),
                    R.drawable.ic_explore_white_24dp,
                    R.string.explore,
                    R.id.nav_explore
            ).addFlag(UserTip.FLAG_NO_DISMISSIBLE));
        }
        if (flagsStorage.isWizardRequired()) {
            content.tips.add(0, new UserTip(
                    getContext().getString(R.string.app_name),
                    //"Для того, чтобы знать, какую мангу вы прочитали, будете читать или уже читаете",
                    getContext().getString(R.string.app_name),
                    R.drawable.ic_explore_white_24dp,
                    R.string.app_name,
                    R.id.nav_explore
            ).addFlag(UserTip.FLAG_DISMISS_BUTTON));
        }
        return content;
    }

    @Deprecated
    private static int getOptimalCells(int items, int columns) {
        if (items <= columns) {
            return items;
        }
        return items - items % columns;
    }
}
