package com.zlm.run.tool;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.zlm.run.R;
import com.zlm.run.entity.NotifyObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 项目名：   Run
 * 包名：     com.zlm.run.tool
 * 文件名：   NotificationUtil
 * 创建者：   PaulZhang
 * 创建时间： 2018/9/19 10:10
 * 描述：     TODO 通知工具类
 */

/**
 * 消息通知工具
 */

public class NotificationUtil {

    private static final String TAG = "NotificationUtil";

    /**
     * 通过定时闹钟发送通知
     *
     * @param context
     * @param notifyObjectMap
     */
    public static void notifyByAlarm(Context context, Map<Integer, NotifyObject> notifyObjectMap) {
        //将数据存储起来
        int count = 0;
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Set<Integer> keySet = notifyObjectMap.keySet();
        for (Integer key0 : keySet) {
            if (!notifyObjectMap.containsKey(key0)) {
                break;
            }

            NotifyObject obj = notifyObjectMap.get(key0);
            if (obj == null) {
                break;
            }

            if (obj.times.size() <= 0) {
                if (obj.firstTime > 0) {
                    try {
                        Map<String, Serializable> map = new HashMap<>();
                        map.put("KEY_NOTIFY_ID", obj.id);
                        map.put("KEY_NOTIFY", NotifyObject.to(obj));
                        AlarmTimerUtil.setAlarmTimer(context, ++count, obj.firstTime, "TIMER_ACTION", map);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                for (long time : obj.times) {
                    if (time > 0) {
                        try {
                            Map<String, Serializable> map = new HashMap<>();
                            map.put("KEY_NOTIFY_ID", obj.id);
                            map.put("KEY_NOTIFY", NotifyObject.to(obj));
                            AlarmTimerUtil.setAlarmTimer(context, ++count, time, "TIMER_ACTION", map);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        SharedPreferences mPreferences = context.getSharedPreferences("SHARE_PREFERENCE_NOTIFICATION", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = mPreferences.edit();
        edit.putInt("KEY_MAX_ALARM_ID", count);
        edit.apply();
    }

    public static void notifyByAlarmByReceiver(Context context, NotifyObject obj) {
        if (context == null || obj == null) return;
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifyMsg(context, obj, obj.id, System.currentTimeMillis(), manager);
    }

    /**
     * 消息通知
     *
     * @param context
     * @param obj
     */
    private static void notifyMsg(Context context, NotifyObject obj, int nid, long time, NotificationManager mNotifyMgr) {
        if (context == null || obj == null) return;
        if (mNotifyMgr == null) {
            mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        if (time <= 0) return;

        //准备intent
        Intent intent = new Intent(context, obj.activityClass);
        if (obj.param != null && obj.param.trim().length() > 0) {
            intent.putExtra("param", obj.param);
        }

        //notification
        Notification notification = null;
        // 构建 PendingIntent
        PendingIntent pi = PendingIntent.getActivity(context, nid, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //版本兼容

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//兼容Android8.0
            String id = "com.zlm.iSport.channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            Uri mUri = Settings.System.DEFAULT_NOTIFICATION_URI;
            CharSequence name = "notice";
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            mChannel.setDescription("sports notice");
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.GREEN);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400});
            mChannel.setSound(mUri,Notification.AUDIO_ATTRIBUTES_DEFAULT);
            Objects.requireNonNull(mNotifyMgr).createNotificationChannel(mChannel);

            Notification.Builder builder = new Notification.Builder(context, id);
            builder.setAutoCancel(true)
                    .setContentIntent(pi)
                    .setContentTitle(obj.title)
                    .setContentText(obj.content)
                    .setOngoing(false)
                    .setSmallIcon(R.mipmap.ic_notificition)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_round))
                    .setWhen(System.currentTimeMillis());
            notification = builder.build();
        } else if (Build.VERSION.SDK_INT >= 23) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "defaults");
            builder.setContentText(obj.content)
                    .setContentTitle(obj.title)
                    .setContentIntent(pi)
                    .setSmallIcon(R.mipmap.ic_notificition)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_round))
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setAutoCancel(true)
                    .setOngoing(false)
                    .setWhen(System.currentTimeMillis());
            notification = builder.build();
        }
        if (notification != null) {
            assert mNotifyMgr != null;
            mNotifyMgr.notify(nid, notification);
        }
    }

    /**
     * 取消所有通知 同时取消定时闹钟
     *
     * @param context
     */
    public static void clearAllNotifyMsg(Context context) {
        try {

            NotificationManager mNotifyMgr =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Objects.requireNonNull(mNotifyMgr).cancelAll();
            SharedPreferences mPreferences = context.getSharedPreferences("SHARE_PREFERENCE_NOTIFICATION", Context.MODE_PRIVATE);
            int max_id = mPreferences.getInt("KEY_MAX_ALARM_ID", 0);
            for (int i = 1; i <= max_id; i++) {
                AlarmTimerUtil.cancelAlarmTimer(context, "TIMER_ACTION", i);
            }
            //清除数据
            mPreferences.edit().remove("KEY_MAX_ALARM_ID").apply();
            LogUtil.i("取消成功");
        } catch (Exception e) {
            Log.e(TAG, "取消通知失败", e);
        }
    }
}
