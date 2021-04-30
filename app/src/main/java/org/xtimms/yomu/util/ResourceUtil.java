package org.xtimms.yomu.util;

import android.content.res.Resources;

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

}
