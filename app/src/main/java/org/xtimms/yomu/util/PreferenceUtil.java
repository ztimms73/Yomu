package org.xtimms.yomu.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.preference.PreferenceManager;

import org.xtimms.yomu.R;

public class PreferenceUtil {

    public static final String GENERAL_THEME = "general_theme";
    public static final String LAST_PAGE = "last_start_page";
    public static final String LAST_CHOOSER = "last_chooser";

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

    public void setLastChooser(final int value) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(LAST_CHOOSER, value);
        editor.apply();
    }

    public final int getLastChooser() {
        return mPreferences.getInt(LAST_CHOOSER, 0);
    }

}
