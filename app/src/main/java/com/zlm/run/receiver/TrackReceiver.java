package com.zlm.run.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import com.baidu.trace.model.StatusCodes;

/**
 * 项目名：   Run
 * 包名：     com.zlm.run.tool
 * 文件名：   TrackReceiver
 * 创建者：   PaulZhang
 * 创建时间： 2018/8/8 0:19
 * 描述：     TODO
 */
public class TrackReceiver extends BroadcastReceiver {

    private PowerManager.WakeLock wakeLock = null;

    public TrackReceiver(PowerManager.WakeLock wakeLock) {
        super();
        this.wakeLock = wakeLock;
    }

    @SuppressLint("Wakelock")
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (Intent.ACTION_SCREEN_OFF.equals(action)) {
            if (null != wakeLock && !(wakeLock.isHeld())) {
                wakeLock.acquire(10*60*1000L /*10 minutes*/);
            }
        } else if (Intent.ACTION_SCREEN_ON.equals(action) || Intent.ACTION_USER_PRESENT.equals(action)) {
            if (null != wakeLock && wakeLock.isHeld()) {
                wakeLock.release();
            }
        } else if (StatusCodes.GPS_STATUS_ACTION.equals(action)) {
            int statusCode = intent.getIntExtra("statusCode", 0);
            String statusMessage = intent.getStringExtra("statusMessage");
            System.out.println(String.format("GPS status, statusCode:%d, statusMessage:%s", statusCode,
                    statusMessage));

        }
    }

}
