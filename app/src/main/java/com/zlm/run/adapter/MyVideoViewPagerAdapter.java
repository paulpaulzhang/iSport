package com.zlm.run.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * 项目名：   Run
 * 包名：     com.zlm.run.adapter
 * 文件名：   MyVideoViewPagerAdapter
 * 创建者：   PaulZhang
 * 创建时间： 2018/7/1 9:50
 * 描述：     轮播视频适配器
 */
public class MyVideoViewPagerAdapter extends PagerAdapter {
    private Context context;
    private List<ViewGroup> viewList;

    public MyVideoViewPagerAdapter(Context context, List<ViewGroup> viewList) {
        this.context = context;
        this.viewList = viewList;
    }
    @Override
    public int getCount() {
        return viewList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = viewList.get(position);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(viewList.get(position));
    }
}
