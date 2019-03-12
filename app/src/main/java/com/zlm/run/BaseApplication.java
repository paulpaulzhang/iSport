package com.zlm.run;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.zlm.run.entity.Constant;

import org.litepal.LitePal;

import cn.bmob.v3.Bmob;
import interfaces.heweather.com.interfacesmodule.view.HeConfig;

/**
 * 项目名：   Run
 * 包名：     com.zlm.run
 * 文件名：   BaseApplication
 * 创建者：   PaulZhang
 * 创建时间： 2018/8/8 14:58
 * 描述：     TODO
 */
public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        /**baiduMap初始化*/
        SDKInitializer.initialize(getApplicationContext());
        /**Bmob初始化**/
        Bmob.initialize(getApplicationContext(), Constant.APP_ID);
        /**LitePal初始化*/
        LitePal.initialize(getApplicationContext());
        /**和风天气初始化*/
        HeConfig.init("HE1802010931481342", "02722b5d743f4be6a91b7f6fc1674787");
        HeConfig.switchToFreeServerNode();
    }
}
