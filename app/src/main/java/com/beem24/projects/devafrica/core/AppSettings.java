package com.beem24.projects.devafrica.core;

import android.content.Context;
import android.content.SharedPreferences;

import com.beem24.projects.devafrica.DevAfrica;

/**
 * Created By Adigun Hammed Olalekan
 * 7/26/2017.
 * Beem24, Inc
 */

public class AppSettings {

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    private static String SETTINGS_PREFERENCE_NAME = "devafrica_settings";
    private static AppSettings appSettings;

    public static synchronized AppSettings getInstance() {
        if(appSettings == null) appSettings = new AppSettings();
        return appSettings;
    }

    private AppSettings() {
        mSharedPreferences = DevAfrica.getApp().getApplicationContext()
        .getSharedPreferences(SETTINGS_PREFERENCE_NAME, Context.MODE_PRIVATE);

        mEditor = mSharedPreferences.edit();
    }
    public void setEnableNotification(boolean enable) {
        mEditor.putBoolean("enable_notification", enable).apply();
    }
    public boolean notificationEnabled() {
        return mSharedPreferences.getBoolean("enable_notification", true);
    }
    public void destroy() {
        mEditor.clear().commit();
    }
}
