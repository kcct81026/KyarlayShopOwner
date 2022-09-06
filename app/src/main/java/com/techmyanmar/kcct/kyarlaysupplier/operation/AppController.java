package com.techmyanmar.kcct.kyarlaysupplier.operation;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import androidx.multidex.MultiDex;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.cloudinary.android.MediaManager;

import java.util.HashMap;
import java.util.Map;

public class AppController extends Application implements ConstanceVariable {

    public static final String TAG = AppController.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static AppController mInstance;
    private Map config = new HashMap();

    //SharedPreferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        MultiDex.install(this);
        initConfig();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {

        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache(getApplicationContext()));
        }



        return this.mImageLoader;
    }



    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public Bitmap getImageBitmap(String url){
        Bitmap bitmap = null;
        LruBitmapCache lu = new LruBitmapCache(getApplicationContext());
        bitmap = lu.getBitmap(url);
        return bitmap;
    }
    /**
     * Starts processing requests on the {@link RequestQueue}.
     */
    public void startProcessingQueue() {
        getRequestQueue().start();
    }
    /**
     * Stops processing requests on the {@link RequestQueue}.
     */
    public void stopProcessingQueue() {
        getRequestQueue().stop();
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    //@Override
    public void onStop () {
        // super.onStop();
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(TAG);
        }
    }

    private void initConfig() {
        try{
            config.put("cloud_name", cloudinary_name);
            config.put("api_key", cloudinary_id);
            config.put("api_secret", cloudinary_api_secret);
        }catch (Exception e){

        }

      /*  try{
            config.put("cloud_name", EncryptedText.decrypt(cloudinary_name));
            config.put("api_key", EncryptedText.decrypt(cloudinary_id));
            config.put("api_secret", EncryptedText.decrypt(cloudinary_api_secret));
        }catch (Exception e){

        }*/
        MediaManager.init(this, config);
    }


}
