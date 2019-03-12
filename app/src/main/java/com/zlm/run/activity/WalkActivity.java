package com.zlm.run.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.today.step.lib.ISportStepInterface;
import com.today.step.lib.TodayStepManager;
import com.today.step.lib.TodayStepService;
import com.zlm.run.R;
import com.zlm.run.bmob.MyUser;
import com.zlm.run.entity.Constant;
import com.zlm.run.tool.LogUtil;
import com.zlm.run.tool.SportsUtil;
import com.zlm.run.view.CustomDialog;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import qiu.niorgai.StatusBarCompat;

public class WalkActivity extends BaseActivity {
    private TextView tv_step_count;
    private TextView tv_km;
    private TextView tv_kcal;
    private ImageButton ib_finish;
    private CustomDialog saving_dialog;

    //循环取当前时刻的步数中间的间隔时间
    private long TIME_INTERVAL_REFRESH = 500;

    private Handler mDelayHandler = new Handler(new TodayStepCounterCall());

    private int mStepSum;
    private int mFirstStep;
    private int mCurrentStep;
    private ISportStepInterface iSportStepInterface;

    private ServiceConnection connection;
    private Intent intent;

    private boolean isFirst = true;
    private boolean isFinished = false;

    private MyUser user;
    private int calorie;
    private double distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk);
        StatusBarCompat.translucentStatusBar(this, true);
        initView();
        initStep();

        user = BmobUser.getCurrentUser(MyUser.class);
    }

    private void initView() {
        tv_step_count = findViewById(R.id.tv_step_count);
        tv_kcal = findViewById(R.id.tv_kcal);
        tv_km = findViewById(R.id.tv_km);
        ib_finish = findViewById(R.id.ib_finish);

        ib_finish.setOnClickListener(this);

        saving_dialog = new CustomDialog(this, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT, R.layout.dialog_saving, R.style.Dialog_Theme, Gravity.CENTER, R.style.pop_anim_style);
        saving_dialog.setCancelable(false);
    }

    private void initStep() {
        //初始化计步模块
        TodayStepManager.init(getApplication());
        //开启计步Service，同时绑定Activity进行aidl通信
        intent = new Intent(this, TodayStepService.class);
        startService(intent);

        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                //Activity和Service通过aidl进行通信
                iSportStepInterface = ISportStepInterface.Stub.asInterface(iBinder);
                try {
                    mStepSum = iSportStepInterface.getCurrentTimeSportStep();
                    if (isFirst) {
                        mFirstStep = mStepSum;
                        isFirst = false;
                    }
                    mCurrentStep = (mStepSum - mFirstStep) / 2;
                    updateStepCount(mCurrentStep);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                mDelayHandler.sendEmptyMessageDelayed(Constant.REFRESH_STEP_WHAT, TIME_INTERVAL_REFRESH);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };

        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_finish:
                finished();
                break;
        }
    }

    private void finished() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("确认退出");
        dialog.setCancelable(false);
        dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                saving_dialog.show();
                if (!isFinished) {
                    unbindService(connection);
                    startService(intent);
                }
                MyUser newUser = new MyUser();
                newUser.setWalk_number(user.getWalk_number() + mCurrentStep);
                newUser.setWalk_kilometers(user.getWalk_kilometers() + distance);
                newUser.setWalk_calorie(user.getWalk_calorie() + calorie);
                newUser.update(user.getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        saving_dialog.dismiss();
                        isFinished = true;
                        if (e == null) {
                            LogUtil.d("更新用户信息成功");
                            Toast.makeText(WalkActivity.this, "数据上传成功", Toast.LENGTH_SHORT).show();
                        } else {
                            LogUtil.d("更新用户信息失败:" + e.getMessage());
                        }
                        finish();
                    }
                });
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void updateStepCount(long step) {
        distance = SportsUtil.getDistanceByStep(step);
        calorie = SportsUtil.getCalorieByStep(step, user.getWeight());
        tv_step_count.setText(step + "");
        tv_km.setText(String.format("%.2f", distance));
        tv_kcal.setText(calorie + "");
    }

    private class TodayStepCounterCall implements Handler.Callback {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case Constant.REFRESH_STEP_WHAT:
                    if (iSportStepInterface != null) {
                        int step = 0;
                        try {
                            step = iSportStepInterface.getCurrentTimeSportStep();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        if (step != mStepSum) {
                            mStepSum = step;
                            mCurrentStep = mStepSum - mFirstStep;
                            updateStepCount(mCurrentStep);
                        }
                    }
                    mDelayHandler.sendEmptyMessageDelayed(Constant.REFRESH_STEP_WHAT, TIME_INTERVAL_REFRESH);
                    break;
                default:
            }
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        finished();
    }
}
