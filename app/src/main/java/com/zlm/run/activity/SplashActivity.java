package com.zlm.run.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zlm.run.R;
import com.zlm.run.bmob.MyUser;
import com.zlm.run.db.DbHelper;
import com.zlm.run.entity.LaunchTimeData;
import com.zlm.run.tool.AnimUtil;
import com.zlm.run.entity.Constant;
import com.zlm.run.tool.LogUtil;
import com.zlm.run.tool.ShareUtils;

import org.litepal.LitePal;

import java.util.Calendar;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;
import qiu.niorgai.StatusBarCompat;


public class SplashActivity extends BaseActivity {
    private TextView mTextView;
    private CircleImageView mCircleImageView;
    private int day;
    private int login_day;
    private MyUser user;
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.HANDLER_SPLASH:
                    if (user != null) {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    } else {
                        startActivity(new Intent(SplashActivity.this, GuideActivity.class));
                    }
                    finish();
                    break;
                case Constant.HANDLER_SPLASH_TV:
                    if (BmobUser.getCurrentUser(MyUser.class) != null) {
                        if (day == ShareUtils.getInt(SplashActivity.this, Constant.PREF_LOGIN_DAY, 0)) {
                            mTextView.setText("你已坚持运动 " + login_day + " 天");
                            AnimUtil.showAction(mTextView);
                            mTextView.setVisibility(View.VISIBLE);

                            AnimUtil.showAction(mCircleImageView);
                            mCircleImageView.setVisibility(View.VISIBLE);
                        } else {
                            ShareUtils.putInt(SplashActivity.this, Constant.PREF_LOGIN_DAY, day);

                            if (isFirst()) {
                                mTextView.setText("你已坚持运动 " + 1 + " 天");
                            } else {
                                ShareUtils.putInt(SplashActivity.this, Constant.PREF_LOGIN_TIME, login_day + 1);
                                mTextView.setText("你已坚持运动 " + ++login_day + " 天");
                                MyUser newUser = new MyUser();
                                newUser.setSports_energy(user.getSports_energy() + 1);
                                newUser.update(user.getObjectId(), new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {
                                            LogUtil.d("更新用户信息成功");
                                        } else {
                                            LogUtil.d("更新用户信息失败:" + e.getMessage());
                                        }
                                    }
                                });

                            }

                            AnimUtil.showAction(mTextView);
                            mTextView.setVisibility(View.VISIBLE);
                            AnimUtil.showAction(mCircleImageView);
                            mCircleImageView.setVisibility(View.VISIBLE);
                        }
                    }

                    break;
                default:
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        StatusBarCompat.translucentStatusBar(this, true);
        mTextView = findViewById(R.id.tv_login_time);
        mCircleImageView = findViewById(R.id.civ_login);
        user = BmobUser.getCurrentUser(MyUser.class);

        if (user != null) {
            if (user.getAvatar() != null) {
                Glide.with(this).load(user.getAvatar().getFileUrl()).into(mCircleImageView);
            } else {
                Glide.with(this).load(Constant.AVATAR_URL).into(mCircleImageView);
            }
        }

        if (isFirst()) {
            createDB();
        }

        Calendar calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_YEAR);
        login_day = ShareUtils.getInt(this, Constant.PREF_LOGIN_TIME, 0);
        handler.sendEmptyMessageDelayed(Constant.HANDLER_SPLASH, 2000);
        handler.sendEmptyMessageDelayed(Constant.HANDLER_SPLASH_TV, 1000);
    }

    private void createDB() {
        LitePal.getDatabase();
        LaunchTimeData mTimeData = DbHelper.getInstance();
        mTimeData.setSix(0);
        mTimeData.setSeven(0);
        mTimeData.setEight(0);
        mTimeData.setNine(0);
        mTimeData.setTen(0);
        mTimeData.setEleven(0);
        mTimeData.setTwelve(0);
        mTimeData.setThirteen(0);
        mTimeData.setFourteen(0);
        mTimeData.setFifteen(0);
        mTimeData.setSixteen(0);
        mTimeData.setSeventeen(0);
        mTimeData.setEighteen(0);
        mTimeData.setNineteen(0);
        mTimeData.setTwenty(0);
        mTimeData.setTwentyOne(0);
        mTimeData.setTwentyTwo(0);
        mTimeData.save();
    }

    private boolean isFirst() {
        if (ShareUtils.getBoolean(this, Constant.PREF_IS_FIRST, true)) {
            ShareUtils.putBoolean(this, Constant.PREF_IS_FIRST, false);
            return true;
        } else {
            return false;
        }
    }
}
