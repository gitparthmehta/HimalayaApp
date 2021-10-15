package com.service.himalaya.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.service.himalaya.Application.Himalaya_applicaation;

public class PreferenceHelper {

    public static SharedPreferences Himalaya_applicaationsharedpreferance;
    public static final String APP_PREFERENCE_NAME = "Himalaya_applicaationsharedpreferance";

    public static void putString(String key, String value) {
        Himalaya_applicaationsharedpreferance = Himalaya_applicaation.getAppContext()
                .getSharedPreferences(APP_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = Himalaya_applicaationsharedpreferance.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void putInt(String key, int value) {
        Himalaya_applicaationsharedpreferance = Himalaya_applicaation.getAppContext()
                .getSharedPreferences(APP_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = Himalaya_applicaationsharedpreferance.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static String getString(String key, String defaultValue) {
        Himalaya_applicaationsharedpreferance = Himalaya_applicaation.getAppContext()
                .getSharedPreferences(APP_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return Himalaya_applicaationsharedpreferance.getString(key, defaultValue);
    }

    public static int getInt(String key, int defaultValue) {
        Himalaya_applicaationsharedpreferance = Himalaya_applicaation.getAppContext()
                .getSharedPreferences(APP_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return Himalaya_applicaationsharedpreferance.getInt(key, defaultValue);
    }

    public static void putBoolean(String key, boolean value) {
        Himalaya_applicaationsharedpreferance = Himalaya_applicaation.getAppContext()
                .getSharedPreferences(APP_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = Himalaya_applicaationsharedpreferance.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        Himalaya_applicaationsharedpreferance = Himalaya_applicaation.getAppContext()
                .getSharedPreferences(APP_PREFERENCE_NAME, Context.MODE_PRIVATE);
        boolean string = Himalaya_applicaationsharedpreferance.getBoolean(key, defaultValue);
        return string;
    }

}
