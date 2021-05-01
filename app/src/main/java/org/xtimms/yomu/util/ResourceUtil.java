package org.xtimms.yomu.util;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.format.DateUtils;

import androidx.annotation.NonNull;
import androidx.annotation.RawRes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ResourceUtil {

    public static String getRawString(Resources resources, @RawRes int resId) {
        InputStream is = null;
        try {
            is = resources.openRawResource(resId);
            String myText;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            int i = is.read();
            while (i != -1) {
                output.write(i);
                i = is.read();
            }
            myText = output.toString();
            return myText;
        } catch (IOException e) {
            return e.getMessage();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @NonNull
    public static String formatTimeRelative(long time) {
        return DateUtils.getRelativeTimeSpanString(time, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
    }

    public static boolean isTablet(Configuration configuration) {
        return configuration.smallestScreenWidthDp >= 600;
    }

    public static int dpToPx(Resources resources, float dp) {
        float density = resources.getDisplayMetrics().density;
        return (int) (dp * density);
    }

}
