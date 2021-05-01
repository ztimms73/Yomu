package org.xtimms.yomu.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.xtimms.yomu.R;
import org.xtimms.yomu.models.CategoryInfo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PreferenceUtil {

    public static final String GENERAL_THEME = "general_theme";
    public static final String REMEMBER_LAST_TAB = "remember_last_tab";
    public static final String LAST_PAGE = "last_start_page";
    public static final String LAST_CHOOSER = "last_chooser";
    public static final String LIBRARY_CATEGORIES = "library_categories";

    private static PreferenceUtil sInstance;

    private final SharedPreferences mPreferences;

    private PreferenceUtil(@NonNull final Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static PreferenceUtil getInstance(@NonNull final Context context) {
        if (sInstance == null) {
            sInstance = new PreferenceUtil(context.getApplicationContext());
        }
        return sInstance;
    }

    public void registerOnSharedPreferenceChangedListener(SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener) {
        mPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    public void unregisterOnSharedPreferenceChangedListener(SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener) {
        mPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    @StyleRes
    public int getGeneralTheme() {
        return getThemeResFromPrefValue(mPreferences.getString(GENERAL_THEME, "light"));
    }

    public void setGeneralTheme(String theme) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(GENERAL_THEME, theme);
        editor.commit();
    }

    public final boolean rememberLastTab() {
        return mPreferences.getBoolean(REMEMBER_LAST_TAB, true);
    }

    @StyleRes
    public static int getThemeResFromPrefValue(String themePrefValue) {
        switch (themePrefValue) {
            case "dark":
                return R.style.Theme_Yomu;
            case "black":
                return R.style.Theme_Yomu_Black;
            case "light":
            default:
                return R.style.Theme_Yomu_Light;
        }
    }

    public void setLastPage(final int value) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(LAST_PAGE, value);
        editor.apply();
    }

    public final int getLastPage() {
        return mPreferences.getInt(LAST_PAGE, 0);
    }

    public void setLastChooser(final int value) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(LAST_CHOOSER, value);
        editor.apply();
    }

    public final int getLastChooser() {
        return mPreferences.getInt(LAST_CHOOSER, 0);
    }

    public List<CategoryInfo> getLibraryCategoryInfos() {
        String data = mPreferences.getString(LIBRARY_CATEGORIES, null);
        if (data != null) {
            Gson gson = new Gson();
            Type collectionType = new TypeToken<List<CategoryInfo>>() {
            }.getType();

            try {
                return gson.fromJson(data, collectionType);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }

        return getDefaultLibraryCategoryInfos();
    }

    public List<CategoryInfo> getDefaultLibraryCategoryInfos() {
        List<CategoryInfo> defaultCategoryInfos = new ArrayList<>(1);
        defaultCategoryInfos.add(new CategoryInfo(CategoryInfo.Category.FAVOURITES, true));
        return defaultCategoryInfos;
    }

}
