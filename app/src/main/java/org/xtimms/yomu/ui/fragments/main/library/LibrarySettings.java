package org.xtimms.yomu.ui.fragments.main.library;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import org.xtimms.yomu.models.Category;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class LibrarySettings {

    private final SharedPreferences mPreferences;

    public LibrarySettings(SharedPreferences preferences) {
        mPreferences = preferences;
    }

    public boolean isRecentEnabled() {
        return mPreferences.getBoolean("shelf.recent_enabled", true);
    }

    public boolean isHistoryEnabled() {
        return mPreferences.getBoolean("shelf.history_enabled", true);
    }

    public int getMaxHistoryRows() {
        return mPreferences.getInt("shelf.history_rows", 1);
    }

    public boolean isFavouritesEnabled() {
        return mPreferences.getBoolean("shelf.favourites_enabled", true);
    }

    public ArrayList<Category> getEnabledCategories(ArrayList<Category> allCategories){
        if (allCategories == null) {
            return new ArrayList<>(0);
        }
        final Set<String> enabledCats = mPreferences.getStringSet("shelf.favourites_categories", null);
        if (enabledCats == null) {
            return allCategories;
        }
        final ArrayList<Category> result = new ArrayList<>(enabledCats.size());
        for (Category o : allCategories) {
            if (enabledCats.contains(String.valueOf(o.id))) {
                result.add(o);
            }
        }
        return result;
    }

    public int getMaxFavouritesRows() {
        return mPreferences.getInt("shelf.favourites_cat_rows", 1);
    }

    public boolean isSavedMangaEnabled() {
        return mPreferences.getBoolean("shelf.savedManga_enabled", true);
    }

    public int getMaxSavedMangaRows() { return mPreferences.getInt("shelf.savedManga_rows", 1); }

    public static void onCategoryAdded(Context context, Category category) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        final Set<String> enabledCats = preferences.getStringSet("shelf.favourites_categories", new HashSet<>(1));
        enabledCats.add(String.valueOf(category.id));
        preferences.edit().putStringSet("shelf.favourites_categories", enabledCats).apply();
    }
}
