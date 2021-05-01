package org.xtimms.yomu.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;

import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import org.xtimms.yomu.R;
import org.xtimms.yomu.misc.ThumbSize;
import org.xtimms.yomu.misc.TransitionDisplayer;

public class ImageUtil {

    private static DisplayImageOptions mOptionsThumb = null;
    private static DisplayImageOptions mOptionsUpdate = null;

    public static void init(Context context) {
        if (!ImageLoader.getInstance().isInited()) {
            int cacheMb = 100;
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                    .defaultDisplayImageOptions(getImageLoaderOptionsBuilder().build())
                    .diskCacheSize(cacheMb * 1024 * 1024) //100 Mb
                    .diskCacheFileCount(200)
                    .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // 2 Mb
                    .build();

            ImageLoader.getInstance().init(config);
        }
        if (mOptionsThumb == null) {
            Drawable holder = ContextCompat.getDrawable(context, R.drawable.placeholder);
            mOptionsThumb = getImageLoaderOptionsBuilder()
                    .showImageOnLoading(holder)
                    .build();
        }

        if (mOptionsUpdate == null) {
            mOptionsUpdate = getImageLoaderOptionsBuilder()
                    .displayer(new TransitionDisplayer())
                    .build();
        }
    }

    private static DisplayImageOptions.Builder getImageLoaderOptionsBuilder() {
        return new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .resetViewBeforeLoading(false)
                .displayer(new FadeInBitmapDisplayer(200, true, true, false));
    }

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

    public static void setThumbnailWithSize(@NonNull ImageView imageView, String url, @Nullable ThumbSize size) {
        ImageLoader.getInstance().displayImage(
                fixUrl(url),
                new ImageViewAware(imageView),
                mOptionsThumb,
                size != null && imageView.getMeasuredWidth() == 0 ? size.toImageSize() : null,
                null,
                null
        );
    }

    public static void updateImage(@NonNull ImageView imageView, String url, String referer) {
        ImageLoader.getInstance().displayImage(
                fixUrl(url),
                imageView,
                mOptionsUpdate
        );
    }

    @Nullable
    public static Palette generatePalette(Bitmap bitmap) {
        if (bitmap == null) return null;
        return Palette.from(bitmap).generate();
    }

}
