package org.xtimms.yomu.util;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

public class ImageUtil {

    private static DisplayImageOptions mOptionsThumb = null;

    public static void recycle(@NonNull ImageView imageView) {
        ImageLoader.getInstance().cancelDisplayTask(imageView);
        final Drawable drawable = imageView.getDrawable();
        if (drawable instanceof BitmapDrawable) {
            //((BitmapDrawable) drawable).getBitmap().recycle();
            imageView.setImageDrawable(null);
        }
        imageView.setTag(null);
    }

    private static String fixUrl(String url) {
        return (!android.text.TextUtils.isEmpty(url) && url.charAt(0) == '/') ? "file://" + url : url;
    }

    public static void setThumbnail(@NonNull ImageView imageView, String url, String referer) {
        if (url != null && url.equals(imageView.getTag())) {
            return;
        }
        imageView.setTag(url);
        ImageLoader.getInstance().displayImage(
                fixUrl(url),
                new ImageViewAware(imageView),
                mOptionsThumb
        );
    }

}
