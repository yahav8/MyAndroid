package com.example.myfinal.PostCode;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsManager {
    private static SettingsManager instance;
    private SharedPreferences prefs;

    // Singleton - מבטיח שיהיה רק עותק אחד של ניהול ההגדרות
    private SettingsManager(Context context) {
        prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
    }

    public static synchronized SettingsManager getInstance(Context context) {
        if (instance == null) {
            instance = new SettingsManager(context.getApplicationContext());
        }
        return instance;
    }

    // פונקציות שליפה מהירות (Getters)
    public boolean isDarkMode() { return prefs.getBoolean("isDarkMode", false); }
    public String getPostTextColor() { return prefs.getString("postTextColor", "#000000"); }
    public String getPostFontType() { return prefs.getString("postFontType", "Default"); }
    public int getPostTextSize() { return prefs.getInt("postTextSize", 18); }
    public String getAppTextSize() { return prefs.getString("appTextSize", "Medium"); }
}