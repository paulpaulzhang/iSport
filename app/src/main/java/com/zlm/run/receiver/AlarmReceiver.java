package com.zlm.run.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zlm.run.entity.NotifyObject;
import com.zlm.run.tool.NotificationUtil;

import java.io.IOException;

/**
 * 项目名：   Run
 * 包名：     com.zlm.run.receiver
 * 文件名：   AlarmReceiver
 * 创建者：   PaulZhang
 * 创建时间： 2018/9/20 6:48
 * 描述：     TODO 闹钟广播
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("TIMER_ACTION".equals(intent.getAction())) {
            String str = intent.getStringExtra("KEY_NOTIFY");
            NotifyObject obj;
            try {
                obj = NotifyObject.from(str);
                if (obj != null) {
                    NotificationUtil.notifyByAlarmByReceiver(context, obj);//立即开启通知
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
