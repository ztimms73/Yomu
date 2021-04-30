package org.xtimms.yomu;

import android.app.Application;

import com.kabouzeid.appthemehelper.ThemeStore;

public class App extends Application {

    private static App app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;

        // default theme
        if (!ThemeStore.isConfigured(this, 1)) {
            ThemeStore.editTheme(this)
                    .primaryColorRes(R.color.md_indigo_500)
                    .accentColorRes(R.color.md_pink_A400)
                    .commit();
        }
    }

    public static App getInstance() {
        return app;
    }

}
