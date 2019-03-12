package com.zlm.run.tool;

/**
 * 项目名：   Run
 * 包名：     com.zlm.run.tool
 * 文件名：   AlarmTimerUtil
 * 创建者：   PaulZhang
 * 创建时间： 2018/9/19 10:05
 * 描述：     TODO 闹钟类
 */

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 * 闹钟定时工具类
 */

public class AlarmTimerUtil {

    /**
     * 设置定时闹钟
     *
     * @param context
     * @param alarmId
     * @param action
     * @param map     要传递的参数
     */
    public static void setAlarmTimer(Context context, int alarmId, long time, String action, Map<String, Serializable> map) {
        Intent myIntent = new Intent();
        myIntent.setComponent(new ComponentName("com.zlm.run", "com.zlm.run.receiver.AlarmReceiver"));
        myIntent.setAction(action);
        if (map != null) {
            for (String key : map.keySet()) {
                myIntent.putExtra(key, map.get(key));
            }
        }
        PendingIntent sender = PendingIntent.getBroadcast(context, alarmId, myIntent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Objects.requireNonNull(alarmManager).setInexactRepeating(AlarmManager.RTC_WAKEUP, time, 24 * 60 * 60 * 1000, sender);
    }

    /**
     * 取消闹钟
     *
     * @param context
     * @param action
     */
    public static void cancelAlarmTimer(Context context, String action, int alarmId) {
        Intent myIntent = new Intent();
        myIntent.setComponent(new ComponentName("com.zlm.run", "com.zlm.run.receiver.AlarmReceiver"));
        myIntent.setAction(action);
        PendingIntent sender = PendingIntent.getBroadcast(context, alarmId, myIntent, 0);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Objects.requireNonNull(alarm).cancel(sender);
    }
}
