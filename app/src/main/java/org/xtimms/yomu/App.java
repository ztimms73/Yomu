package org.xtimms.yomu;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.kabouzeid.appthemehelper.ThemeStore;

import org.xtimms.yomu.misc.AppSettings;
import org.xtimms.yomu.misc.CookieStore;
import org.xtimms.yomu.misc.CrashHandler;
import org.xtimms.yomu.misc.FileLogger;
import org.xtimms.yomu.misc.UpdateHelper;
import org.xtimms.yomu.util.ImageUtil;
import org.xtimms.yomu.util.NetworkUtil;

import java.util.HashMap;
import java.util.Map;

public class App extends Application {

    private static App app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        final AppSettings settings = AppSettings.get(this);

        FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();

        //Default value
        Map<String, Object> defaultValue = new HashMap<>();
        defaultValue.put(UpdateHelper.KEY_UPDATE_ENABLE, false);
        defaultValue.put(UpdateHelper.KEY_UPDATE_VERSION, "1.0");
        defaultValue.put(UpdateHelper.KEY_UPDATE_URL, "https://github.com/ztimms73/Yomu/releases");

        remoteConfig.setDefaultsAsync(defaultValue);
        remoteConfig.fetch(5).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                remoteConfig.fetchAndActivate();
            }
        });

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
