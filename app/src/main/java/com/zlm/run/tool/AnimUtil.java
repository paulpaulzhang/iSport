package com.zlm.run.tool;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;

import com.zlm.run.R;

/**
 * 项目名：   Run
 * 包名：     com.zlm.run.tool
 * 文件名：   AnimUtil
 * 创建者：   PaulZhang
 * 创建时间： 2018/9/3 19:58
 * 描述：     TODO
 */
public class AnimUtil {
    public static void showAction(View view) {
        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -0.1f, Animation.RELATIVE_TO_SELF, 0.0f);
        animation.setDuration(500L);
        view.startAnimation(animation);
    }

    public static void hideAction(View view) {
        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -0.1f);
        animation.setDuration(500L);
        view.startAnimation(animation);
    }

    public static void readyAction(View view){
        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
        animation.setDuration(300L);
        view.startAnimation(animation);
    }
}
