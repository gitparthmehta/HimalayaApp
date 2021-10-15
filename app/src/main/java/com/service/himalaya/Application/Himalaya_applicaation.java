package com.service.himalaya.Application;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.service.himalaya.Services.MyBackgroundService;

public class Himalaya_applicaation extends Application {

    public static Context appContext;// instance
    private static Himalaya_applicaation mInstance = null;
    private RequestQueue mRequestQueue;


    public static final String TAG = Himalaya_applicaation.class.getSimpleName();

    public static SharedPreferences getSharedPreference(String name, int mode) {
        SharedPreferences preferences = getInstance().getSharedPreferences(
                name, mode);
        return preferences;
    }

    public static SharedPreferences.Editor getSharedPreferenceEditor(
            String name, int mode) {
        SharedPreferences preferences = getInstance().getSharedPreferences(
                name, mode);
        SharedPreferences.Editor editor = preferences.edit();
        return editor;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();

        mInstance = this;
//        startService(new Intent(this, MyBackgroundService.class));

    }

    public static synchronized Himalaya_applicaation getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
    public static Context getAppContext() {
        return appContext;
    }
}