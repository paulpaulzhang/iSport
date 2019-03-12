package com.zlm.run.tool;

import android.util.Log;

/**
 * 项目名：   Run
 * 包名：     com.zlm.run.tool
 * 文件名：   LogUtil
 * 创建者：   PaulZhang
 * 创建时间： 2018/7/1 15:23
 * 描述：     Log工具类
 */

public class LogUtil {

    public static final String TAG = "RunRunRun";
    public static final boolean DEBUG = true;

    public static void d(String text) {
        if (DEBUG) {
            Log.d(TAG, text);
        }
    }

    public static void i(String text) {
        if (DEBUG) {
            Log.i(TAG, text);
        }
    }

    public static void w(String text) {
        if (DEBUG) {
            Log.w(TAG, text);
        }
    }

    public static void e(String text) {
        if (DEBUG) {
            Log.e(TAG, text);
        }
    }
}
