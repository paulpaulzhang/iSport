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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.today.step.lib.ISportStepInterface;
import com.today.step.lib.TodayStepManager;
import com.today.step.lib.TodayStepService;
import com.zlm.run.R;
import com.zlm.run.bmob.MyUser;
import com.zlm.run.tool.AnimUtil;
import com.zlm.run.entity.Constant;
import com.zlm.run.tool.LogUtil;
import com.zlm.run.tool.SportsUtil;
import com.zlm.run.view.CustomDialog;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import qiu.niorgai.StatusBarCompat;

import static com.zlm.run.entity.Constant.RUNNING_TIME;

public class IndoorRunningActivity extends BaseActivity {
    private ImageButton ib_indoor_pause;
    private ImageButton ib_indoor_start;
    private ImageButton ib_indoor_finish;
    private TextView tv_indoor_km;
    private TextView tv_indoor_speed;
    private TextView tv_indoor_time;
    private TextView tv_indoor_calorie;

    private ImageView iv_go;
    private ImageView iv_three;
    private ImageView iv_two;
    private ImageView iv_one;

    //循环取当前时刻的步数中间的间隔时间
    private long TIME_INTERVAL_REFRESH = 500;

    private Handler mDelayHandler = new Handler(new TodayStepCounterCall());

    private int mStepSum;
    private int mFirstStep;
    private int mCurrentStep;

    private ISportStepInterface iSportStepInterface;

    private ServiceConnection connection;
    private Intent intent;

    private int time = 0;
    private boolean isFirst = true;

    private MyUser user;
    private int calorie;
    private double distance;

    private int flagTime = 0;
    private long flagStep;
    private boolean isStarted = true;
    private int isPaused = 0; //0为初始状态,1为暂停,2为跑步
    private boolean isFinished = false;

    private CustomDialog saving_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indoor_running);
        StatusBarCompat.translucentStatusBar(this, true);

        initReadyAnim();
        initView();
        initStep();

        mDelayHandler.sendEmptyMessageDelayed(Constant.TIME_INDOOR_MSG, RUNNING_TIME);

        user = BmobUser.getCurrentUser(MyUser.class);
    }

    private void initView() {
        ib_indoor_pause = findViewById(R.id.ib_indoor_pause);
        ib_indoor_start = findViewById(R.id.ib_indoor_start);
        ib_indoor_finish = findViewById(R.id.ib_indoor_finish);
        tv_indoor_km = findViewById(R.id.tv_indoor_km);
        tv_indoor_speed = findViewById(R.id.tv_indoor_speed);
        tv_indoor_time = findViewById(R.id.tv_indoor_time);
        tv_indoor_calorie = findViewById(R.id.tv_indoor_calorie);

        ib_indoor_pause.setOnClickListener(this);
        ib_indoor_start.setOnClickListener(this);
        ib_indoor_finish.setOnClickListener(this);

        saving_dialog = new CustomDialog(this, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT, R.layout.dialog_saving, R.style.Dialog_Theme, Gravity.CENTER, R.style.pop_anim_style);
        saving_dialog.setCancelable(false);
    }

    private void initReadyAnim() {
        iv_go = findViewById(R.id.iv_go);
        iv_one = findViewById(R.id.iv_one);
        iv_two = findViewById(R.id.iv_two);
        iv_three = findViewById(R.id.iv_three);

        mDelayHandler.sendEmptyMessageDelayed(Constant.IN_READY_THREE, 1000);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_indoor_start:
                AnimUtil.hideAction(ib_indoor_start);
                ib_indoor_start.setVisibility(View.GONE);
                AnimUtil.hideAction(ib_indoor_finish);
                ib_indoor_finish.setVisibility(View.GONE);
                AnimUtil.showAction(ib_indoor_pause);
                ib_indoor_pause.setVisibility(View.VISIBLE);
                onStartStep();
                break;
            case R.id.ib_indoor_pause:
                AnimUtil.hideAction(ib_indoor_pause);
                ib_indoor_pause.setVisibility(View.GONE);
                AnimUtil.showAction(ib_indoor_start);
                ib_indoor_start.setVisibility(View.VISIBLE);
                AnimUtil.showAction(ib_indoor_finish);
                ib_indoor_finish.setVisibility(View.VISIBLE);
                onPauseStep();
                break;
            case R.id.ib_indoor_finish:
                if (distance <= 1.0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("本次运动距离过短,结束将无法保存记录,坚持一下啊")
                            .setPositiveButton("确认退出", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    isStarted = false;
                                    isPaused = 0;
                                    isFinished = true;
                                    unbindService(connection);
                                    stopService(intent);
                                    finish();
                                }
                            })
                            .setNegativeButton("坚持一下", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                    builder.show();
                } else {
                    onFinish();
                }
                break;
            default:
        }
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
                    if (isStarted) {
                        mCurrentStep = mStepSum - mFirstStep;
                        updateStepCount(mCurrentStep);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                ;
                mDelayHandler.sendEmptyMessageDelayed(Constant.REFRESH_STEP_WHAT, TIME_INTERVAL_REFRESH);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                LogUtil.i("服务连接已断开");
            }
        };

        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @SuppressLint("SetTextI18n")
    private void updateStepCount(long step) {
        distance = SportsUtil.getDistanceByStep(step);
        calorie = SportsUtil.getCalorieByStep(step, user.getWeight());
        tv_indoor_speed.setText(String.format("%.2f", SportsUtil.getSpeedByStep(step, TIME_INTERVAL_REFRESH * time / 1000)));
        tv_indoor_km.setText(String.format("%.2f", distance));
        tv_indoor_calorie.setText(calorie + "");
    }

    //暂停
    private void onPauseStep() {
        isPaused = 1;
        isStarted = false;
        mDelayHandler.removeMessages(Constant.TIME_INDOOR_MSG);
    }

    //开始
    private void onStartStep() {
        isPaused = 2;
        isStarted = true;
        if (isFinished) {
            startService(intent);
            bindService(intent, connection, Context.BIND_AUTO_CREATE);
            mCurrentStep = 0;
            flagTime = 0;
        }
        mDelayHandler.sendEmptyMessageDelayed(Constant.TIME_INDOOR_MSG, RUNNING_TIME);
    }

    //结束
    private void onFinish() {
        saving_dialog.show();
        isStarted = false;
        isPaused = 0;
        isFinished = true;
        unbindService(connection);
        stopService(intent);

        MyUser newUser = new MyUser();
        newUser.setRunning_calorie(user.getRunning_calorie() + calorie);
        newUser.setRunning_kilometers(user.getRunning_kilometers() + distance);
        newUser.update(user.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                saving_dialog.dismiss();
                if (e == null) {
                    LogUtil.d("更新用户信息成功");
                } else {
                    LogUtil.d("更新用户信息失败:" + e.getMessage());
                }
                finish();
            }
        });
    }

    private class TodayStepCounterCall implements Handler.Callback {
        @SuppressLint("SetTextI18n")
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case Constant.REFRESH_STEP_WHAT:
                    time++;
                    if (iSportStepInterface != null) {
                        int step = 0;
                        try {
                            step = iSportStepInterface.getCurrentTimeSportStep();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        if (step != mStepSum) {
                            mStepSum = step;

                            if (!isStarted && isPaused == 1) {
                                flagStep = mStepSum;
                                isPaused = 0;
                            }
                            if (isStarted && isPaused == 2) {
                                mFirstStep = (int) (mFirstStep + mStepSum - flagStep);
                                isPaused = 0;
                            }
                            if (isStarted) {
                                mCurrentStep = mStepSum - mFirstStep;
                                updateStepCount(mCurrentStep);
                                LogUtil.i("========== mCurrentStep:   " + mCurrentStep + "  ** mStepSum   " + mStepSum + "    **** mFirstStep    " + mFirstStep);
                            }
                        }
                    }
                    mDelayHandler.sendEmptyMessageDelayed(Constant.REFRESH_STEP_WHAT, TIME_INTERVAL_REFRESH);
                    break;
                case Constant.TIME_INDOOR_MSG:
                    if (tv_indoor_time != null) {
                        if (time < 9) {
                            tv_indoor_time.setText("0" + ++flagTime);
                        } else {
                            tv_indoor_time.setText(++flagTime + "");
                        }
                    }
                    mDelayHandler.sendEmptyMessageDelayed(Constant.TIME_INDOOR_MSG, RUNNING_TIME);
                    break;
                case Constant.IN_READY_THREE:
                    AnimUtil.readyAction(iv_three);
                    iv_three.setVisibility(View.GONE);
                    mDelayHandler.sendEmptyMessageDelayed(Constant.IN_READY_TWO, 1000);
                    break;
                case Constant.IN_READY_TWO:
                    AnimUtil.readyAction(iv_two);
                    iv_two.setVisibility(View.GONE);
                    mDelayHandler.sendEmptyMessageDelayed(Constant.IN_READY_ONE, 1000);
                    break;
                case Constant.IN_READY_ONE:
                    AnimUtil.readyAction(iv_one);
                    iv_one.setVisibility(View.GONE);
                    mDelayHandler.sendEmptyMessageDelayed(Constant.IN_READY_GO, 1000);
                    break;
                case Constant.IN_READY_GO:
                    AnimUtil.readyAction(iv_go);
                    iv_go.setVisibility(View.GONE);
                    StatusBarCompat.translucentStatusBar(IndoorRunningActivity.this, false);
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
        if (isStarted) {
            Toast.makeText(this, "您还未结束运动哦！", Toast.LENGTH_SHORT).show();
        } else {
            finish();
        }
    }
}
