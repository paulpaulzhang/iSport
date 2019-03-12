package com.zlm.run.tool;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 项目名：   Run
 * 包名：     com.zlm.run.tool
 * 文件名：   NetUtil
 * 创建者：   PaulZhang
 * 创建时间： 2018/8/3 23:22
 * 描述：     网络检测
 */
public class NetUtil {

    /**
     * 检测网络状态是否联通
     *
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (null != info && info.isConnected() && info.isAvailable()) {
                return true;
            }
        } catch (Exception e) {
            LogUtil.e("current network is not available");
            return false;
        }
        return false;
    }
}
