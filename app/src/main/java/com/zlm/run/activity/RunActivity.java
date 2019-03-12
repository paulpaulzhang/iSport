package com.zlm.run.activity;

import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.zlm.run.R;
import com.zlm.run.fragment.IndoorRunningFragment;
import com.zlm.run.fragment.OutdoorRunningFragment;

import java.util.ArrayList;
import java.util.List;

public class RunActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private TabLayout mTablayout;
    private List<Fragment> fragmentList;
    private List<String> titleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);
        initView();
    }

    private void initView() {
        mViewPager = findViewById(R.id.run_vp);
        mTablayout = findViewById(R.id.run_tl);
        mToolbar = findViewById(R.id.run_tb);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        titleList = new ArrayList<>();
        titleList.add("室外跑");
        titleList.add("室内跑");

        for (int i = 0; i < titleList.size(); i++) {
            mTablayout.addTab(mTablayout.newTab().setText(titleList.get(i)));
        }

        fragmentList = new ArrayList<>();
        fragmentList.add(new OutdoorRunningFragment());
        fragmentList.add(new IndoorRunningFragment());

        //ViewPager预加载
        mViewPager.setOffscreenPageLimit(fragmentList.size());
        //监听viewPager滑动事件
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //给ViewPager设置适配器，若次适配器在fragment内部使用，则FragmentManger应为getChildFragmentManager()
        mViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return titleList.get(position);
            }
        });
        //将ViewPager与Tablayout绑定起来
        mTablayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
