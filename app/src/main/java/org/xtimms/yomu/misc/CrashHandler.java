package org.xtimms.yomu.misc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.xtimms.yomu.util.ErrorUtil;

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private final Thread.UncaughtExceptionHandler mDefaultHandler;
    private final SharedPreferences mPreferences;

    public CrashHandler(Context context) {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        mPreferences = context.getSharedPreferences("crash", Context.MODE_PRIVATE);
    }

    @Override
    @SuppressLint("ApplySharedPref")
    public void uncaughtException(@NonNull Thread t, Throwable e) {
        mPreferences.edit()
                .putLong("last", System.currentTimeMillis())
                .putString("class", e.getClass().getName())
                .putString("message", e.getMessage())
                .putString("stack_trace", ErrorUtil.getStackTrace(e))
                .commit();
        mDefaultHandler.uncaughtException(t, e);
    }

    public void clear() {
        mPreferences.edit().clear().apply();
    }

    public boolean wasCrashed() {
        return mPreferences.contains("last");
    }

    @Nullable
    public static CrashHandler get() {
        Thread.UncaughtExceptionHandler handler = Thread.getDefaultUncaughtExceptionHandler();
        return handler instanceof CrashHandler ? (CrashHandler) handler : null;
    }

    @Nullable
    public String getErrorClassName() {
        return mPreferences.getString("class", null);
    }

    @Nullable
    public String getErrorMessage() {
        return mPreferences.getString("message", null);
    }

    @Nullable
    public String getErrorStackTrace() {
        return mPreferences.getString("stack_trace", null);
    }

    public long getErrorTimestamp() {
        return mPreferences.getLong("last", 0);
    }
}
