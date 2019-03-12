package com.zlm.run.tool;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * 项目名：   Run
 * 包名：     com.zlm.run.tool
 * 文件名：   LogUtil
 * 创建者：   PaulZhang
 * 创建时间： 2018/7/21 15:23
 * 描述：     SharePreference工具类
 */

public class ShareUtils {

    public static void putString(Context mContext, String key, String value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        prefs.edit().putString(key, value).apply();
    }

    public static String getString(Context mContext, String key, String defaultValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        return prefs.getString(key, defaultValue);
    }

    public static void putInt(Context mContext, String key, int value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        prefs.edit().putInt(key, value).apply();
    }

    public static int getInt(Context mContext, String key, int defaultValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        return prefs.getInt(key, defaultValue);
    }

    public static void putBoolean(Context mContext, String key, boolean value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        prefs.edit().putBoolean(key, value).apply();
    }

    public static boolean getBoolean(Context mContext, String key, boolean defaultValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        return prefs.getBoolean(key, defaultValue);
    }

    public static void deleteShare(Context mContext, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        prefs.edit().remove(key).apply();
    }

    public static void deleteAllShare(Context mContext) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        prefs.edit().clear().apply();
    }
}
