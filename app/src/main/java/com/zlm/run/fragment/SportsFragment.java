package com.zlm.run.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.wang.avi.AVLoadingIndicatorView;
import com.zlm.run.R;
import com.zlm.run.activity.AssignmentActivity;
import com.zlm.run.activity.CourseActivity;
import com.zlm.run.activity.CreateActivity;
import com.zlm.run.activity.RidingActivity;
import com.zlm.run.activity.RunActivity;
import com.zlm.run.activity.WalkActivity;
import com.zlm.run.activity.YogaActivity;
import com.zlm.run.adapter.MyVideoViewPagerAdapter;
import com.zlm.run.bmob.MyUser;
import com.zlm.run.entity.Constant;
import com.zlm.run.tool.LogUtil;
import com.zlm.run.tool.ResourcesUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.bmob.v3.BmobUser;
import interfaces.heweather.com.interfacesmodule.bean.air.now.AirNow;
import interfaces.heweather.com.interfacesmodule.bean.weather.lifestyle.Lifestyle;
import interfaces.heweather.com.interfacesmodule.bean.weather.now.Now;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;


public class SportsFragment extends Fragment implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener,
        MediaPlayer.OnCompletionListener, View.OnClickListener {
    private ViewPager mViewPager;
    private MyVideoViewPagerAdapter mAdapter;

    /*** 当前页面索引 ***/
    private int currentItem = 0;

    /*** 上一个页面索引 ***/
    private int lastItem = 0;

    /*** viewpager的根视图数据集合 ***/
    private List<ViewGroup> mViewList;

    /*** 页面的视频控件集合 ***/
    private List<VideoView> mVideoViewList;

    /*** 页面视频缓冲图集合 ***/
    private List<View> mCacheImgList;

    /*** 页面播放按钮集合***/
    private List<ImageView> mPlayButton;

    private String[] paths = {Constant.RUN_URL, Constant.RIDE_URL, Constant.WALK_URL, Constant.YOGA_URL};
    private int[] cacheImgs = {R.drawable.run_cache_img, R.drawable.ride_cache_img, R.drawable.walk_cache_img, R.drawable.yoga_cache_img};

    private ConstraintLayout run_run_layout;
    private ConstraintLayout run_bike_layout;
    private ConstraintLayout run_walk_layout;
    private ConstraintLayout run_yoga_layout;
    private ConstraintLayout cl_update;
    private Toolbar mToolbar;
    private TextView tv_run;
    private TextView tv_ride;
    private TextView tv_walk;
    private TextView tv_yoga;
    private ImageView iv_weather;
    private TextView tv_weather;
    private TextView tv_weather_status;
    private TextView tv_city;
    private TextView tv_weather_aqi;
    private TextView tv_suggestion;
    private ImageView iv_assignment;
    private ImageView iv_course;
    private ImageView iv_share;
    private AVLoadingIndicatorView av_loading;
    private List<String> permissionList;

    private LocationClient mLocationClient;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sports, container, false);
        setHasOptionsMenu(true);
        mLocationClient = new LocationClient(getContext());
        mLocationClient.registerLocationListener(new MyWeatherLocationListener());

        findView(view);
        requestDefaultWeather();
        initUserData();
        initVideoData();
        initVideoView();
        requestPermission();
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void findView(View view) {
        mViewPager = view.findViewById(R.id.run_view_pager);
        run_run_layout = view.findViewById(R.id.run_run_layout);
        run_bike_layout = view.findViewById(R.id.run_bike_layout);
        run_walk_layout = view.findViewById(R.id.run_walk_layout);
        run_yoga_layout = view.findViewById(R.id.run_yoga_layout);
        cl_update = view.findViewById(R.id.cl_update);
        tv_run = view.findViewById(R.id.run);
        tv_ride = view.findViewById(R.id.bike);
        tv_walk = view.findViewById(R.id.walk);
        tv_yoga = view.findViewById(R.id.yoga);
        iv_weather = view.findViewById(R.id.iv_weather);
        tv_weather = view.findViewById(R.id.tv_weather);
        tv_weather_aqi = view.findViewById(R.id.tv_weather_aqi);
        tv_weather_status = view.findViewById(R.id.tv_weather_status);
        tv_city = view.findViewById(R.id.tv_city);
        tv_suggestion = view.findViewById(R.id.tv_suggestion);
        iv_assignment = view.findViewById(R.id.iv_assignment);
        iv_course = view.findViewById(R.id.iv_course);
        iv_share = view.findViewById(R.id.iv_share);
        av_loading = view.findViewById(R.id.av_loading);
        mToolbar = view.findViewById(R.id.tb_sports);

        ((AppCompatActivity) (Objects.requireNonNull(getActivity()))).setSupportActionBar(mToolbar);
        mToolbar.setTitle("运动");

        run_run_layout.setOnClickListener(this);
        run_bike_layout.setOnClickListener(this);
        run_walk_layout.setOnClickListener(this);
        run_yoga_layout.setOnClickListener(this);
        iv_assignment.setOnClickListener(this);
        iv_course.setOnClickListener(this);
        iv_share.setOnClickListener(this);
        cl_update.setOnClickListener(this);
    }

    private void initUserData() {
        MyUser user = BmobUser.getCurrentUser(MyUser.class);
        tv_run.setText(String.format("%.2f", user.getRunning_kilometers()) + " 公里");
        tv_ride.setText(String.format("%.2f", user.getRiding_kilometers()) + " 公里");
        tv_walk.setText(user.getWalk_number() + " 步");
        tv_yoga.setText(user.getYoga_time() + " 分");
    }

    private void requestPermission() {
        permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            requestPermissions(permissions, 1);
        } else {
            requestWeather();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(getContext(), "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    requestWeather();
                }
            default:
        }
    }

    private void initVideoData() {
        mViewList = new ArrayList<>();
        mVideoViewList = new ArrayList<>();
        mCacheImgList = new ArrayList<>();
        mPlayButton = new ArrayList<>();

        for (int i = 0; i < paths.length; i++) {
            ViewGroup view = (ViewGroup) View.inflate(getContext(), R.layout.video_view_pager_item, null);
            final VideoView videoView = view.findViewById(R.id.video_view);
            final ImageView playButton = view.findViewById(R.id.video_view_play_img);
            final ImageView cacheImg = view.findViewById(R.id.video_view_cache_img);

            videoView.setVideoURI(Uri.parse(paths[i]));
            cacheImg.setBackground(Objects.requireNonNull(getActivity()).getDrawable(cacheImgs[i]));
            setListener(videoView);

            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.video_view_play_img:
                            if (!videoView.isPlaying()) {
                                videoView.start();
                                playButton.setVisibility(View.INVISIBLE);
                            }
                            break;
                        default:
                            break;
                    }
                }
            });

            mViewList.add(view);
            mVideoViewList.add(videoView);
            mPlayButton.add(playButton);
            mCacheImgList.add(cacheImg);
        }

    }

    private void initVideoView() {
        mViewPager.setOffscreenPageLimit(4);
        mAdapter = new MyVideoViewPagerAdapter(getActivity(), mViewList);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setPageMargin((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                lastItem = currentItem;
                currentItem = position;
                if (mVideoViewList.get(lastItem) != null && mVideoViewList.get(lastItem).isPlaying()) {
                    mVideoViewList.get(lastItem).resume();
                    mVideoViewList.get(lastItem).pause();
                    mPlayButton.get(lastItem).setVisibility(View.VISIBLE);
                    mCacheImgList.get(lastItem).setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.run_run_layout:
                startActivity(new Intent(getContext(), RunActivity.class));
                break;
            case R.id.run_bike_layout:
                startActivity(new Intent(getContext(), RidingActivity.class));
                break;
            case R.id.run_walk_layout:
                startActivity(new Intent(getContext(), WalkActivity.class));
                break;
            case R.id.run_yoga_layout:
                startActivity(new Intent(getContext(), YogaActivity.class));
                break;
            case R.id.cl_update:
                requestWeather();
                break;
            case R.id.iv_assignment:
                startActivity(new Intent(getContext(), AssignmentActivity.class));
                break;
            case R.id.iv_course:
                startActivity(new Intent(getContext(), CourseActivity.class));
                break;
            case R.id.iv_share:
                startActivity(new Intent(getContext(), CreateActivity.class));
                break;
            default:
        }
    }


    private void requestWeather() {
        av_loading.setVisibility(View.VISIBLE);
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        mLocationClient.setLocOption(option);

        mLocationClient.start();
    }

    private void requestDefaultWeather() {
        HeWeather.getWeatherNow(getContext(), "CN101030100", new HeWeather.OnResultWeatherNowBeanListener() {
            @Override
            public void onError(Throwable throwable) {
                if (av_loading.getVisibility() == View.VISIBLE) {
                    av_loading.setVisibility(View.INVISIBLE);
                }
                LogUtil.i("天气 " + throwable.getMessage());
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(List<Now> list) {
                if (av_loading.getVisibility() == View.VISIBLE) {
                    av_loading.setVisibility(View.INVISIBLE);
                }
                Now now = list.get(0);
                tv_weather.setText(now.getNow().getTmp() + "℃");
                tv_weather_status.setText(now.getNow().getCond_txt());
                tv_city.setText(now.getBasic().getLocation());
                iv_weather.setImageResource(ResourcesUtils.getDrawableId(getContext(), "w" + now.getNow().getCond_code()));
                Toast.makeText(getContext(), now.getUpdate().getLoc() + " 更新", Toast.LENGTH_SHORT).show();
            }
        });
        HeWeather.getAirNow(getContext(), "CN101030100", new HeWeather.OnResultAirNowBeansListener() {
            @Override
            public void onError(Throwable throwable) {
                if (av_loading.getVisibility() == View.VISIBLE) {
                    av_loading.setVisibility(View.INVISIBLE);
                }
                LogUtil.i("空气 " + throwable.getMessage());
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(List<AirNow> list) {
                if (av_loading.getVisibility() == View.VISIBLE) {
                    av_loading.setVisibility(View.INVISIBLE);
                }
                AirNow airNow = list.get(0);
                tv_weather_aqi.setText("空气" + airNow.getAir_now_city().getQlty() + ":" + airNow.getAir_now_city().getAqi());
            }
        });
        HeWeather.getWeatherLifeStyle(getContext(), "CN101030100", new HeWeather.OnResultWeatherLifeStyleBeanListener() {
            @Override
            public void onError(Throwable throwable) {
                if (av_loading.getVisibility() == View.VISIBLE) {
                    av_loading.setVisibility(View.INVISIBLE);
                }
                LogUtil.i("生活 " + throwable.getMessage());
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(List<Lifestyle> list) {
                if (av_loading.getVisibility() == View.VISIBLE) {
                    av_loading.setVisibility(View.INVISIBLE);
                }
                Lifestyle lifestyle = list.get(0);
                tv_suggestion.setText(lifestyle.getLifestyle().get(3).getTxt());
            }
        });
    }

    private class MyWeatherLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(final BDLocation bdLocation) {
            HeWeather.getWeatherNow(getContext(), bdLocation.getLongitude() + "," + bdLocation.getLatitude(), new HeWeather.OnResultWeatherNowBeanListener() {
                @Override
                public void onError(Throwable throwable) {
                    if (av_loading.getVisibility() == View.VISIBLE) {
                        av_loading.setVisibility(View.INVISIBLE);
                    }
                    LogUtil.i("天气 " + throwable.getMessage());
                }

                @SuppressLint("SetTextI18n")
                @Override
                public void onSuccess(List<Now> list) {
                    if (av_loading.getVisibility() == View.VISIBLE) {
                        av_loading.setVisibility(View.INVISIBLE);
                    }
                    Now now = list.get(0);
                    tv_weather.setText(now.getNow().getTmp() + "℃");
                    tv_weather_status.setText(now.getNow().getCond_txt());
                    tv_city.setText(now.getBasic().getLocation());
                    iv_weather.setImageResource(ResourcesUtils.getDrawableId(getContext(), "w" + now.getNow().getCond_code()));
                    Toast.makeText(getContext(), now.getUpdate().getLoc() + " 更新", Toast.LENGTH_SHORT).show();
                }
            });
            HeWeather.getAirNow(getContext(), bdLocation.getCity(), new HeWeather.OnResultAirNowBeansListener() {
                @Override
                public void onError(Throwable throwable) {
                    if (av_loading.getVisibility() == View.VISIBLE) {
                        av_loading.setVisibility(View.INVISIBLE);
                    }
                    LogUtil.i("空气 " + throwable.getMessage());
                }

                @SuppressLint("SetTextI18n")
                @Override
                public void onSuccess(List<AirNow> list) {
                    if (av_loading.getVisibility() == View.VISIBLE) {
                        av_loading.setVisibility(View.INVISIBLE);
                    }
                    AirNow airNow = list.get(0);
                    tv_weather_aqi.setText("空气" + airNow.getAir_now_city().getQlty() + ":" + airNow.getAir_now_city().getAqi());
                }
            });
            HeWeather.getWeatherLifeStyle(getContext(), bdLocation.getLongitude() + "," + bdLocation.getLatitude(), new HeWeather.OnResultWeatherLifeStyleBeanListener() {
                @Override
                public void onError(Throwable throwable) {
                    if (av_loading.getVisibility() == View.VISIBLE) {
                        av_loading.setVisibility(View.INVISIBLE);
                    }
                    LogUtil.i("生活 " + throwable.getMessage());
                }

                @SuppressLint("SetTextI18n")
                @Override
                public void onSuccess(List<Lifestyle> list) {
                    if (av_loading.getVisibility() == View.VISIBLE) {
                        av_loading.setVisibility(View.INVISIBLE);
                    }
                    Lifestyle lifestyle = list.get(0);
                    tv_suggestion.setText(lifestyle.getLifestyle().get(3).getTxt());
                }
            });
            mLocationClient.stop();
        }
    }

    private void setListener(VideoView videoView) {
        videoView.setOnPreparedListener(this);
        videoView.setOnCompletionListener(this);
        videoView.setOnErrorListener(this);
        videoView.setOnInfoListener(this);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (mVideoViewList.get(currentItem) != null) {
            mPlayButton.get(currentItem).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int what, int i1) {
        return true;
    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
                if (i == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                    mCacheImgList.get(currentItem).setVisibility(View.INVISIBLE);
                }
                return true;
            }
        });
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (!isVisibleToUser) {
            if (mVideoViewList != null && mVideoViewList.size() > 0 && mVideoViewList.get(currentItem) != null && mVideoViewList.get(currentItem).isPlaying()) {
                mVideoViewList.get(currentItem).resume();
                mVideoViewList.get(currentItem).pause();
                mCacheImgList.get(currentItem).setVisibility(View.VISIBLE);
                mPlayButton.get(currentItem).setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initUserData();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mVideoViewList != null && mVideoViewList.size() > 0 && mVideoViewList.get(currentItem) != null && mVideoViewList.get(currentItem).isPlaying()) {
            mVideoViewList.get(currentItem).resume();
            mVideoViewList.get(currentItem).pause();
            mCacheImgList.get(currentItem).setVisibility(View.VISIBLE);
            mPlayButton.get(currentItem).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mVideoViewList != null && mVideoViewList.size() > 0 && mVideoViewList.get(currentItem) != null && mVideoViewList.get(currentItem).isPlaying()) {
            mVideoViewList.get(currentItem).resume();
            mVideoViewList.get(currentItem).pause();
            mCacheImgList.get(currentItem).setVisibility(View.VISIBLE);
            mPlayButton.get(currentItem).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mVideoViewList.get(currentItem) != null) {
            mVideoViewList.get(currentItem).stopPlayback();
            mVideoViewList.get(currentItem).suspend();
        }
        clearList();
    }


    private void clearList() {
        mViewList.clear();
        mVideoViewList.clear();
        mCacheImgList.clear();
        mPlayButton.clear();
    }

}

