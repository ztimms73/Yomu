package org.xtimms.yomu;

import android.app.Application;

import com.kabouzeid.appthemehelper.ThemeStore;

import org.xtimms.yomu.misc.AppSettings;
import org.xtimms.yomu.misc.CookieStore;
import org.xtimms.yomu.misc.CrashHandler;
import org.xtimms.yomu.misc.FileLogger;
import org.xtimms.yomu.util.ImageUtil;
import org.xtimms.yomu.util.NetworkUtil;

public class App extends Application {

    private static App app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        final AppSettings settings = AppSettings.get(this);

        // default theme
        if (!ThemeStore.isConfigured(this, 1)) {
            ThemeStore.editTheme(this)
                    .primaryColorRes(R.color.md_indigo_500)
                    .accentColorRes(R.color.md_pink_A400)
                    .commit();
        }

        CrashHandler mCrashHandler = new CrashHandler(this);
        Thread.setDefaultUncaughtExceptionHandler(mCrashHandler);
        ImageUtil.init(this);
        FileLogger.init(this);
        CookieStore.getInstance().init(this);
        NetworkUtil.init(this, settings.isUseTor());
    }

    public static App getInstance() {
        return app;
    }

}
