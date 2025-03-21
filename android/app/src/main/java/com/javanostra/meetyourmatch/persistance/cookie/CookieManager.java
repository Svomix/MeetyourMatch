package com.javanostra.meetyourmatch.persistance.cookie;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class CookieManager {
    private static final String PREFS_NAME = "CookiePrefs";
    private static final String COOKIE_KEY = "cookie";

    private SharedPreferences sharedPreferences;

    public CookieManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveCookie(String cookie) {
        sharedPreferences.edit().putString(COOKIE_KEY, cookie).apply();
    }

    public String getCookie() {
        Log.d("CookieManager", sharedPreferences.getString(COOKIE_KEY, "null"));
        return sharedPreferences.getString(COOKIE_KEY, null);
    }
}

