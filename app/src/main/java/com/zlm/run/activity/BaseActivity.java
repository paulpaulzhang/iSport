package com.zlm.run.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * 项目名：   Run
 * 包名：     com.zlm.run.activity
 * 文件名：   BaseActivity
 * 创建者：   PaulZhang
 * 创建时间： 2018/7/21 16:06
 * 描述：     activity基类
 */
public class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    public static int screenWidth = 0;
    public static int screenHeight = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getScreenSize();
    }

    @Override
    public void onClick(View view) {

    }

    /**
     * 获取屏幕尺寸
     */
    private void getScreenSize() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenHeight = dm.heightPixels;
        screenWidth = dm.widthPixels;
    }
}
