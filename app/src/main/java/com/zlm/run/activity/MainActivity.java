package com.zlm.run.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.zlm.run.R;
import com.zlm.run.bmob.MyUser;
import com.zlm.run.db.DbHelper;
import com.zlm.run.entity.LaunchTimeData;
import com.zlm.run.entity.NotifyObject;
import com.zlm.run.fragment.ArticleFragment;
import com.zlm.run.fragment.CircleFragment;
import com.zlm.run.fragment.SportsFragment;
import com.zlm.run.fragment.UserFragment;
import com.zlm.run.tool.LogUtil;
import com.zlm.run.tool.NotificationUtil;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;

import cn.bmob.v3.BmobUser;

/**
 * 应用主界面
 **/
public class MainActivity extends BaseActivity {
    private ViewPager main_view_pager;
    private BottomNavigationBar main_bottom_navigation_bar;
    private List<Fragment> fragmentList;
    private long time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
        onDynamicPush();
    }

    private void initData() {
        fragmentList = new ArrayList<>();
        fragmentList.add(new SportsFragment());
        fragmentList.add(new ArticleFragment());
        fragmentList.add(new CircleFragment());
        fragmentList.add(new UserFragment());
    }

    private void initView() {
        main_view_pager = findViewById(R.id.main_view_pager);
        main_bottom_navigation_bar = findViewById(R.id.main_bottom_navigation_bar);
        main_view_pager.setOffscreenPageLimit(fragmentList.size());
        main_view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                main_bottom_navigation_bar.selectTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        main_view_pager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        });

        main_bottom_navigation_bar.setMode(BottomNavigationBar.MODE_FIXED_NO_TITLE);
        main_bottom_navigation_bar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);

        main_bottom_navigation_bar.addItem(new BottomNavigationItem(R.drawable.outline_directions_run_white_48dp, "运动"))
                .addItem(new BottomNavigationItem(R.drawable.outline_chrome_reader_mode_white_48dp, "资讯"))
                .addItem(new BottomNavigationItem(R.drawable.outline_bubble_chart_white_48dp, "圈子"))
                .addItem(new BottomNavigationItem(R.drawable.outline_account_circle_white_48dp, "我的"))
                .setFirstSelectedPosition(0)
                .initialise();
        main_view_pager.setCurrentItem(0);
        main_bottom_navigation_bar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                main_view_pager.setCurrentItem(position);
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {

            }
        });
    }

    private void onDynamicPush() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        LaunchTimeData mTimeData = DbHelper.getInstance();
        switch (hour) {
            case 6:
                mTimeData.setSix(mTimeData.getSix() + 1);
                break;
            case 7:
                mTimeData.setSeven(mTimeData.getSeven() + 1);
                break;
            case 8:
                mTimeData.setEight(mTimeData.getEight() + 1);
                break;
            case 9:
                mTimeData.setNine(mTimeData.getNine() + 1);
                break;
            case 10:
                mTimeData.setTen(mTimeData.getTen() + 1);
                break;
            case 11:
                mTimeData.setEleven(mTimeData.getEleven() + 1);
                break;
            case 12:
                mTimeData.setTwelve(mTimeData.getTwelve() + 1);
                break;
            case 13:
                mTimeData.setThirteen(mTimeData.getThirteen() + 1);
                break;
            case 14:
                mTimeData.setFourteen(mTimeData.getFourteen() + 1);
                break;
            case 15:
                mTimeData.setFifteen(mTimeData.getFifteen() + 1);
                break;
            case 16:
                mTimeData.setSixteen(mTimeData.getSixteen() + 1);
                break;
            case 17:
                mTimeData.setSeventeen(mTimeData.getSeventeen() + 1);
                break;
            case 18:
                mTimeData.setEighteen(mTimeData.getEighteen() + 1);
                break;
            case 19:
                mTimeData.setNineteen(mTimeData.getNineteen() + 1);
                break;
            case 20:
                mTimeData.setTwenty(mTimeData.getTwenty() + 1);
                break;
            case 21:
                mTimeData.setTwentyOne(mTimeData.getTwentyOne() + 1);
                break;
            case 22:
                mTimeData.setTwentyTwo(mTimeData.getTwentyTwo() + 1);
                break;
            default:
        }
        mTimeData.save();

        int[] time = new int[]{
                mTimeData.getSix(), mTimeData.getSeven(), mTimeData.getEight(), mTimeData.getNine(),
                mTimeData.getTen(), mTimeData.getEleven(), mTimeData.getTwelve(), mTimeData.getThirteen(),
                mTimeData.getFourteen(), mTimeData.getFifteen(), mTimeData.getSixteen(), mTimeData.getSeventeen(),
                mTimeData.getEighteen(), mTimeData.getNineteen(), mTimeData.getTwenty(), mTimeData.getTwentyOne(),
                mTimeData.getTwentyTwo()};

        Map<Integer, Integer> data = new TreeMap<>();
        for (int i = 6; i <= 22; i++) {
            data.put(time[i - 6], i);
        }
        Set<Integer> keySet = data.keySet();
        Integer[] keys = keySet.toArray(new Integer[keySet.size()]);

        LogUtil.i("time " + data.get(keys[keys.length - 1]));
        calendar.set(Calendar.HOUR_OF_DAY, data.get(keys[keys.length - 1]));
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (System.currentTimeMillis() > calendar.getTimeInMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        NotifyObject object = new NotifyObject();
        object.id = 1;
        object.content = "新的一天，你运动了吗？";
        object.title = "运动啦！";
        object.firstTime = calendar.getTimeInMillis();
        object.activityClass = SplashActivity.class;

        @SuppressLint("UseSparseArrays") Map<Integer, NotifyObject> objects = new HashMap<>();
        objects.put(object.id, object);
        NotificationUtil.notifyByAlarm(this, objects);
    }

    @Override
    public void onBackPressed() {
        if (time == 0) {
            time = System.currentTimeMillis();
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
        } else {
            if (System.currentTimeMillis() - time < 2000) {
                finish();
            } else {
                time = System.currentTimeMillis();
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

