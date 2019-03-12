package com.zlm.run.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zlm.run.R;

import java.util.ArrayList;
import java.util.List;

import qiu.niorgai.StatusBarCompat;

public class GuideActivity extends BaseActivity {

    private ViewPager mViewPager;
    private List<View> viewList;
    private View view1, view2, view3;
    private TextView jumpButton;
    private LinearLayout linearLayout;
    private ImageView point_red;

    private int distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        StatusBarCompat.translucentStatusBar(this, true);
        initView();

    }

    private void initView() {
        mViewPager = findViewById(R.id.mViewPager);
        jumpButton = findViewById(R.id.btn_jump);
        point_red = findViewById(R.id.point_red);
        linearLayout = findViewById(R.id.linear_layout);

        view1 = View.inflate(this, R.layout.guide_start_one, null);
        view2 = View.inflate(this, R.layout.guide_start_two, null);
        view3 = View.inflate(this, R.layout.guide_start_three, null);

        viewList = new ArrayList<>();
        viewList.add(view1);
        viewList.add(view2);
        viewList.add(view3);

        point_red.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                distance = linearLayout.getChildAt(1).getLeft() - linearLayout.getChildAt(0).getLeft();
            }
        });

        mViewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return viewList.size();
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                //判断view是不是属于object
                return view == object;
            }

            //添加view
            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                container.addView(viewList.get(position));
                return viewList.get(position);
            }

            //删除view
            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView(viewList.get(position));
            }
        });

        //viewPager的屏幕滚动监听器
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //测出页面滚动时小红点移动的距离，并通过setLayoutParams(params)不断更新其位置
                float leftMargin = distance * (position + positionOffset);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) point_red.getLayoutParams();
                params.leftMargin = Math.round(leftMargin);
                point_red.setLayoutParams(params);
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        jumpButton.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        jumpButton.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        jumpButton.setVisibility(View.GONE);

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        view3.findViewById(R.id.btn_start).setOnClickListener(this);
        jumpButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_jump:
            case R.id.btn_start:
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
        }
    }
}
