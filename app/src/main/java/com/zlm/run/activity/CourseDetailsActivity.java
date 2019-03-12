package com.zlm.run.activity;

import android.content.Intent;
import android.os.Bundle;

import com.zlm.run.tool.ResourcesUtils;

import qiu.niorgai.StatusBarCompat;

/**
 * 项目名：   Run
 * 包名：     com.zlm.run.activity
 * 文件名：   CourseDetailsActivity
 * 创建者：   PaulZhang
 * 创建时间： 2018/10/1 15:51
 * 描述：     TODO
 */
public class CourseDetailsActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String id = intent.getStringExtra("res_id");
        setContentView(ResourcesUtils.getLayoutId(this, id));
        StatusBarCompat.translucentStatusBar(this);
    }
}
