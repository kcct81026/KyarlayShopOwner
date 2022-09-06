package com.techmyanmar.kcct.kyarlaysupplier.operation;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

public class LruBitmapCache extends LruCache<String, Bitmap> implements
        ImageLoader.ImageCache {
    public static int getDefaultLruCacheSize(Context context) {
        /*final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
*/

        final int DEFAULT_CACHE_SIZE_PROPORTION = 8;

        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        int memoryClass = manager.getMemoryClass();
        int memoryClassInKilobytes = memoryClass * 1024;
        int cacheSize = memoryClassInKilobytes / DEFAULT_CACHE_SIZE_PROPORTION;

        return cacheSize;
    }

    public LruBitmapCache(Context context) {
        this(getDefaultLruCacheSize(context));
    }

    public LruBitmapCache(int sizeInKiloBytes) {
        super(sizeInKiloBytes);
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getRowBytes() * value.getHeight() / 1024;
    }

    @Override
    public Bitmap getBitmap(String url) {
        return get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        put(url, bitmap);
    }
}