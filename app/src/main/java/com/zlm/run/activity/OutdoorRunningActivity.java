package com.zlm.run.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.trace.model.SortType;
import com.baidu.trace.model.StatusCodes;
import com.zlm.run.R;
import com.zlm.run.bmob.MyUser;
import com.zlm.run.receiver.TrackReceiver;
import com.zlm.run.tool.AnimUtil;
import com.zlm.run.tool.BitmapUtil;
import com.zlm.run.tool.CommonUtil;
import com.zlm.run.entity.Constant;
import com.zlm.run.tool.LogUtil;
import com.zlm.run.tool.MapUtil;
import com.zlm.run.tool.ShareUtils;
import com.zlm.run.tool.SportsUtil;
import com.zlm.run.view.CustomDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import qiu.niorgai.StatusBarCompat;

import static com.zlm.run.entity.Constant.RUNNING_TIME;

public class OutdoorRunningActivity extends BaseActivity {
    private Button btn_outdoor_map;
    private ImageButton ib_outdoor_back;
    private ImageButton ib_outdoor_pause;
    private ImageButton ib_outdoor_start;
    private ImageButton ib_outdoor_finish;
    private TextView tv_outdoor_km;
    private TextView tv_outdoor_speed;
    private TextView tv_outdoor_time;
    private TextView tv_outdoor_calorie;
    private MapView mv_outdoor;

    private ImageView iv_go;
    private ImageView iv_three;
    private ImageView iv_two;
    private ImageView iv_one;

    private MapUtil mapUtil;
    private LocationClient mLocationClient;
    private boolean isFirst = true;
    private boolean isPaused = false;
    private boolean isStarted = false;
    private List<LatLng> latLngs;

    private PowerManager powerManager = null;
    private PowerManager.WakeLock wakeLock = null;
    private TrackReceiver trackReceiver = null;
    public boolean isRegisterReceiver = false;

    private CustomDialog saving_dialog;

    private int time = 0;
    private int flagTime = 0;
    private double flagDistance;
    private double distance = 0; //里程值
    private boolean flagTarget = true;

    private int target;

    private MyUser user;
    private int calorie;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.DISTANCE_MSG:
                    calorie = SportsUtil.getCalorieByDistance(distance / 1000, user.getWeight());
                    if (tv_outdoor_km != null) {
                        tv_outdoor_km.setText(String.format("%.2f", distance / 1000));
                    }
                    if (distance / 1000 > target && flagTarget) {
                        Toast.makeText(OutdoorRunningActivity.this, "目标已完成！", Toast.LENGTH_SHORT).show();
                        flagTarget = false;
                    }
                    if (tv_outdoor_calorie != null) {
                        tv_outdoor_calorie.setText(calorie + "");
                    }
                    flagTime++;
                    handler.sendEmptyMessageDelayed(Constant.DISTANCE_MSG, 5 * 1000);
                    break;
                case Constant.TIME_MSG:
                    if (tv_outdoor_time != null) {
                        if (time < 9) {
                            tv_outdoor_time.setText("0" + ++time);
                        } else {
                            tv_outdoor_time.setText(++time + "");
                        }
                    }
                    handler.sendEmptyMessageDelayed(Constant.TIME_MSG, RUNNING_TIME);
                    break;
                case Constant.SPEED_MSG:
                    ++flagTime;
                    if (tv_outdoor_speed != null) {
                        if (distance == 0) {
                            tv_outdoor_speed.setText("0.00");
                        } else {
                            tv_outdoor_speed.setText(String.format("%.2f", SportsUtil.getSpeedByDistance(distance, flagTime)));
                        }
                    }
                    handler.sendEmptyMessageDelayed(Constant.SPEED_MSG, 1000);
                    break;
                case Constant.OUT_READY_THREE:
                    AnimUtil.readyAction(iv_three);
                    iv_three.setVisibility(View.GONE);
                    handler.sendEmptyMessageDelayed(Constant.OUT_READY_TWO, 1000);
                    break;
                case Constant.OUT_READY_TWO:
                    AnimUtil.readyAction(iv_two);
                    iv_two.setVisibility(View.GONE);
                    handler.sendEmptyMessageDelayed(Constant.OUT_READY_ONE, 1000);
                    break;
                case Constant.OUT_READY_ONE:
                    AnimUtil.readyAction(iv_one);
                    iv_one.setVisibility(View.GONE);
                    handler.sendEmptyMessageDelayed(Constant.OUT_READY_GO, 1000);
                    break;
                case Constant.OUT_READY_GO:
                    AnimUtil.readyAction(iv_go);
                    iv_go.setVisibility(View.GONE);
                    StatusBarCompat.translucentStatusBar(OutdoorRunningActivity.this, false);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outdoor_running);

        initReadyAnim();

        target = ShareUtils.getInt(this, "run_target", 2);

        StatusBarCompat.translucentStatusBar(this, true);
        BitmapUtil.init();
        initView();
        mapUtil = MapUtil.getINSTANCE();
        mapUtil.init(mv_outdoor);

        latLngs = new ArrayList<>();
        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new RunningLocationListener());
        initLocation();

        handler.sendEmptyMessageDelayed(Constant.TIME_MSG, RUNNING_TIME);
        handler.sendEmptyMessageDelayed(Constant.DISTANCE_MSG, 5 * 1000);
        handler.sendEmptyMessageDelayed(Constant.SPEED_MSG, 1000);

        user = BmobUser.getCurrentUser(MyUser.class);
    }

    private void initReadyAnim() {
        iv_go = findViewById(R.id.iv_go);
        iv_one = findViewById(R.id.iv_one);
        iv_two = findViewById(R.id.iv_two);
        iv_three = findViewById(R.id.iv_three);

        handler.sendEmptyMessageDelayed(Constant.OUT_READY_THREE, 1000);
    }

    private void initView() {
        btn_outdoor_map = findViewById(R.id.btn_outdoor_map);
        ib_outdoor_back = findViewById(R.id.ib_outdoor_back);
        ib_outdoor_pause = findViewById(R.id.ib_outdoor_pause);
        ib_outdoor_start = findViewById(R.id.ib_outdoor_start);
        ib_outdoor_finish = findViewById(R.id.ib_outdoor_finish);
        tv_outdoor_km = findViewById(R.id.tv_outdoor_km);
        tv_outdoor_speed = findViewById(R.id.tv_outdoor_speed);
        tv_outdoor_time = findViewById(R.id.tv_outdoor_time);
        tv_outdoor_calorie = findViewById(R.id.tv_outdoor_calorie);
        mv_outdoor = findViewById(R.id.mv_outdoor);

        btn_outdoor_map.setOnClickListener(this);
        ib_outdoor_back.setOnClickListener(this);
        ib_outdoor_pause.setOnClickListener(this);
        ib_outdoor_start.setOnClickListener(this);
        ib_outdoor_finish.setOnClickListener(this);

        saving_dialog = new CustomDialog(this, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT, R.layout.dialog_saving, R.style.Dialog_Theme, Gravity.CENTER, R.style.pop_anim_style);
        saving_dialog.setCancelable(false);
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(2000);
        option.setOpenGps(true);
        option.setCoorType("bd09ll");
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
        notification();
        registerReceiver();
    }

    private void notification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//兼容Android8.0
            String id = "com.zlm.iSport.running";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            CharSequence name = "running";
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            mChannel.setDescription("sports notice");
            Objects.requireNonNull(notificationManager).createNotificationChannel(mChannel);

            notification = new Notification.Builder(this, id)
                    .setAutoCancel(true)
                    .setContentTitle("跑步")
                    .setContentText("正在跑步")
                    .setSmallIcon(R.mipmap.ic_notificition)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                    .setWhen(System.currentTimeMillis())
                    .build();
        } else if (Build.VERSION.SDK_INT >= 23) {
            notification = new NotificationCompat.Builder(this, "defaults")
                    .setContentText("正在跑步")
                    .setContentTitle("跑步")
                    .setSmallIcon(R.mipmap.ic_notificition)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                    .setAutoCancel(true)
                    .setWhen(System.currentTimeMillis())
                    .build();
        }
        if (notification != null) {
            mLocationClient.enableLocInForeground(1001, notification);
        }

    }

    private class RunningLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            LatLng latLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            if (isFirst) {
                mapUtil.updateStatus(latLng, true);
                isFirst = false;
            } else {
                latLngs.add(latLng);
                if (latLngs.size() >= 2) {
                    distance = distance + DistanceUtil.getDistance(latLngs.get(latLngs.size() - 2), latLngs.get(latLngs.size() - 1));
                }
                mapUtil.updateStatus(latLng, true);
                mapUtil.drawHistoryTrack(latLngs, SortType.asc);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_outdoor_map:
                int width = mv_outdoor.getMeasuredWidth();
                int height = mv_outdoor.getMeasuredHeight();
                float radius = (float) (Math.sqrt(width * width + height * height) / 2);
                Animator animator = ViewAnimationUtils.createCircularReveal(mv_outdoor, width / 2, height / 2, 0, radius);
                animator.setDuration(500L);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                mv_outdoor.setVisibility(View.VISIBLE);
                ib_outdoor_back.setVisibility(View.VISIBLE);
                animator.start();
                break;
            case R.id.ib_outdoor_back:
                width = mv_outdoor.getMeasuredWidth();
                height = mv_outdoor.getMeasuredHeight();
                radius = (float) (Math.sqrt(width * width + height * height) / 2);
                animator = ViewAnimationUtils.createCircularReveal(mv_outdoor, width / 2, height / 2, radius, 0);
                animator.setDuration(500L);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mv_outdoor.setVisibility(View.GONE);
                    }
                });
                animator.start();
                ib_outdoor_back.setVisibility(View.GONE);
                break;
            case R.id.ib_outdoor_start:
                Toast.makeText(this, "开始运动", Toast.LENGTH_SHORT).show();
                mLocationClient.start();
                notification();
                distance = flagDistance;
                handler.sendEmptyMessageDelayed(Constant.TIME_MSG, RUNNING_TIME);
                handler.sendEmptyMessageDelayed(Constant.DISTANCE_MSG, 5 * 1000);
                handler.sendEmptyMessageDelayed(Constant.SPEED_MSG, 1000);

                AnimUtil.hideAction(ib_outdoor_start);
                ib_outdoor_start.setVisibility(View.GONE);
                AnimUtil.hideAction(ib_outdoor_finish);
                ib_outdoor_finish.setVisibility(View.GONE);
                AnimUtil.showAction(ib_outdoor_pause);
                ib_outdoor_pause.setVisibility(View.VISIBLE);

                break;
            case R.id.ib_outdoor_pause:
                Toast.makeText(this, "运动已暂停", Toast.LENGTH_SHORT).show();
                mLocationClient.stop();

                flagDistance = distance;
                handler.removeMessages(Constant.DISTANCE_MSG);
                handler.removeMessages(Constant.TIME_MSG);
                handler.removeMessages(Constant.SPEED_MSG);

                AnimUtil.hideAction(ib_outdoor_pause);
                ib_outdoor_pause.setVisibility(View.GONE);
                AnimUtil.showAction(ib_outdoor_start);
                ib_outdoor_start.setVisibility(View.VISIBLE);
                AnimUtil.showAction(ib_outdoor_finish);
                ib_outdoor_finish.setVisibility(View.VISIBLE);
                break;
            case R.id.ib_outdoor_finish:
                if (distance / 1000 <= 1.0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("本次运动距离过短,结束将无法保存记录,坚持一下啊")
                            .setPositiveButton("确认退出", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    flagDistance = 0;
                                    handler.removeMessages(Constant.DISTANCE_MSG);
                                    handler.removeMessages(Constant.TIME_MSG);
                                    handler.removeMessages(Constant.SPEED_MSG);
                                    mLocationClient.stop();
                                    unRegisterReceiver();
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
                    saving_dialog.show();
                    flagDistance = 0;
                    handler.removeMessages(Constant.DISTANCE_MSG);
                    handler.removeMessages(Constant.TIME_MSG);
                    handler.removeMessages(Constant.SPEED_MSG);
                    mLocationClient.stop();
                    unRegisterReceiver();
                    MyUser newUser = new MyUser();
                    newUser.setRunning_kilometers(user.getRunning_kilometers() + distance / 1000);
                    newUser.setRunning_calorie(user.getRunning_calorie() + calorie);
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
                break;
            default:
        }
    }

    /**
     * 注册广播（电源锁、GPS状态）
     */
    private void registerReceiver() {
        if (isRegisterReceiver) {
            return;
        }
        if (wakeLock == null) {
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "track upload");
        }
        if (trackReceiver == null) {
            trackReceiver = new TrackReceiver(wakeLock);
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(StatusCodes.GPS_STATUS_ACTION);
        registerReceiver(trackReceiver, filter);
        isRegisterReceiver = true;
    }

    private void unRegisterReceiver() {
        if (!isRegisterReceiver) {
            return;
        }
        if (trackReceiver != null) {
            unregisterReceiver(trackReceiver);
        }
        isRegisterReceiver = false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意请求权限", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;

            default:
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "您还未结束运动哦！", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapUtil.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapUtil.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapUtil.clear();
        latLngs.clear();
        BitmapUtil.clear();
    }
}
