package org.xtimms.yomu.misc;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import org.xtimms.yomu.ui.fragments.main.library.LibrarySettings;

import java.lang.ref.WeakReference;

public class AppSettings {

    @Nullable
    private static WeakReference<AppSettings> sInstanceReference = null;

    private final SharedPreferences mPreferences;
    public final LibrarySettings librarySettings;

    @SuppressWarnings("deprecation")
    private AppSettings(Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        librarySettings = new LibrarySettings(mPreferences);
    }

    public static AppSettings get(Context context) {
        AppSettings instance = sInstanceReference == null ? null : sInstanceReference.get();
        if (instance == null) {
            instance = new AppSettings(context);
            sInstanceReference = new WeakReference<>(instance);
        }
        return instance;
    }

    public boolean isUseTor() {
        return mPreferences.getBoolean("use_tor", false);
    }

    public String getAppLocale() {
        return mPreferences.getString("lang", "");
    }

    public int getAppTheme() {
        try {
            return Integer.parseInt(mPreferences.getString("theme", "0"));
        } catch (Exception e) {
            return 0;
        }
    }
}