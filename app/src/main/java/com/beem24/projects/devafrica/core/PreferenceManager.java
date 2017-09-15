package com.beem24.projects.devafrica.core;

import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.beem24.projects.devafrica.DevAfrica;
import com.beem24.projects.devafrica.entities.User;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 6/13/2017.
 */

public class PreferenceManager {

    private static PreferenceManager sInstance;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    public static synchronized PreferenceManager getInstance() {
        if(sInstance == null) sInstance = new PreferenceManager();return sInstance;
    }
    private PreferenceManager() {
        mSharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(
                DevAfrica.getApp().getApplicationContext()
        );
        mEditor = mSharedPreferences.edit();
    }
    public void save(String id) {
        mEditor.putString("user_id", id).commit();
    }
    public String getUserID() {
        return mSharedPreferences.getString("user_id", String.valueOf(0));
    }
    public void save(JSONObject user) {
        mEditor.putString("user_", user.toString()).commit();
    }
    public boolean hasSession() {
        return !TextUtils.isEmpty(getToken());
    }
    public void saveCampusPreference(String pref) {
        mEditor.putString("campus_preference", pref).commit();
    }
    public void saveToken(String token) {
        mEditor.putString("token_", token).commit();
    }
    public String getToken() {
        return mSharedPreferences.getString("token_", "");
    }
    public String getCampusIDs() {
        return mSharedPreferences.getString("campus_preference", "");
    }
    public User getUser() {
        String json = mSharedPreferences.getString("user_", "{}");
        try {
            Log.d(DevAfrica.TAG, json + "_");
            return User.from(new JSONObject(json).getJSONObject("data"));
        }catch (JSONException je) {
            try {
                return User.from(new JSONObject(json));
            }catch (JSONException e) {}
        }
        return new User();
    }
    public void saveFCMToken(String token) {
        mEditor.putString("fcm_token", token);
        mEditor.putBoolean("has_saved_fcm_token", true).commit();
    }
    public boolean hasSavedToken() {
        return mSharedPreferences.getBoolean("has_saved_fcm_token", false);
    }
    public String getFCMToken() {
        return mSharedPreferences.getString("fcm_token", "");
    }
    public String userJSON() {
        return mSharedPreferences.getString("user_", "{}");
    }
    public void logOut() {
        mEditor.clear().commit();
    }
}
