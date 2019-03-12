package com.zlm.run.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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
import com.zlm.run.tool.BitmapUtil;
import com.zlm.run.entity.Constant;
import com.zlm.run.tool.LogUtil;
import com.zlm.run.tool.MapUtil;
import com.zlm.run.tool.SportsUtil;
import com.zlm.run.view.CustomDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import qiu.niorgai.StatusBarCompat;

public class RidingActivity extends BaseActivity {
    private MapView riding_map_view;
    private TextView riding_distance;
    private TextView riding_time;
    private TextView riding_calorie;
    private Button riding_trace_button;
    private Button riding_finish_button;

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
    private double flagDistance;
    private double distance = 0; //里程值

    private MyUser user;
    private int calorie;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.DISTANCE_MSG:
                    calorie = SportsUtil.getCalorieByDistance(distance / 1000, user.getWeight());
                    if (riding_distance != null) {
                        riding_distance.setText(String.format("%.2f" + "公里", distance / 1000));
                    }
                    if (riding_calorie != null) {
                        riding_calorie.setText(calorie + "千卡");
                    }
                    handler.sendEmptyMessageDelayed(Constant.DISTANCE_MSG, 5 * 1000);
                    break;
                case Constant.TIME_MSG:
                    if (riding_time != null) {
                        if (time < 9) {
                            riding_time.setText("0" + ++time + "分");
                        } else {
                            riding_time.setText(++time + "分");
                        }

                    }
                    handler.sendEmptyMessageDelayed(Constant.TIME_MSG, 60 * 1000);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_riding);
        StatusBarCompat.translucentStatusBar(this, true);
        BitmapUtil.init();
        mapUtil = MapUtil.getINSTANCE();
        initView();
        mapUtil.init(riding_map_view);

        latLngs = new ArrayList<>();
        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new RidingLocationListener());
        initLocation();

        user = BmobUser.getCurrentUser(MyUser.class);
    }

    private void initView() {
        riding_map_view = findViewById(R.id.riding_map_view);
        riding_distance = findViewById(R.id.riding_distance);
        riding_time = findViewById(R.id.riding_time);
        riding_calorie = findViewById(R.id.riding_calorie);
        riding_trace_button = findViewById(R.id.riding_trace_button);
        riding_finish_button = findViewById(R.id.riding_finish_button);

        riding_trace_button.setOnClickListener(this);
        riding_finish_button.setOnClickListener(this);

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
            String id = "com.zlm.iSport.riding";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            CharSequence name = "riding";
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            mChannel.setDescription("sports notice");
            Objects.requireNonNull(notificationManager).createNotificationChannel(mChannel);

            notification = new Notification.Builder(this, id)
                    .setAutoCancel(true)
                    .setContentTitle("骑行")
                    .setContentText("正在骑行")
                    .setSmallIcon(R.mipmap.ic_notificition)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                    .setWhen(System.currentTimeMillis())
                    .build();
        } else if (Build.VERSION.SDK_INT >= 23) {
            notification = new NotificationCompat.Builder(this, "defaults")
                    .setContentText("正在骑行")
                    .setContentTitle("骑行")
                    .setSmallIcon(R.mipmap.ic_notificition)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                    .setAutoCancel(true)
                    .setWhen(System.currentTimeMillis())
                    .build();
        }
        if (notification != null) {
            mLocationClient.enableLocInForeground(1002, notification);
        }

    }

    private class RidingLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            LatLng latLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            if (isStarted) {
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
            } else {
                mapUtil.updateStatus(latLng, true);
            }

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.riding_trace_button:
                if (isStarted) {
                    if (isPaused) {
                        isPaused = false;
                        mLocationClient.start();
                        notification();
                        distance = flagDistance;
                        handler.sendEmptyMessageDelayed(Constant.TIME_MSG, 60 * 1000);
                        handler.sendEmptyMessageDelayed(Constant.DISTANCE_MSG, 5 * 1000);

                        riding_finish_button.setVisibility(View.INVISIBLE);
                        riding_trace_button.setText(R.string.pause);
                        riding_trace_button.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.white, null));
                        riding_trace_button.setBackground(ResourcesCompat.getDrawable(getResources(),
                                R.drawable.button_pause_riding, null));
                    } else {
                        isPaused = true;
                        mLocationClient.stop();

                        flagDistance = distance;
                        handler.removeMessages(Constant.DISTANCE_MSG);
                        handler.removeMessages(Constant.TIME_MSG);

                        riding_finish_button.setVisibility(View.VISIBLE);
                        riding_trace_button.setText(R.string.start);
                        riding_trace_button.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.white, null));
                        riding_trace_button.setBackground(ResourcesCompat.getDrawable(getResources(),
                                R.drawable.button_start_riding, null));
                    }
                } else {
                    isStarted = true;
                    handler.sendEmptyMessageDelayed(Constant.TIME_MSG, 60 * 1000);
                    handler.sendEmptyMessageDelayed(Constant.DISTANCE_MSG, 5 * 1000);

                    riding_finish_button.setVisibility(View.INVISIBLE);
                    riding_trace_button.setText(R.string.pause);
                    riding_trace_button.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.white, null));
                    riding_trace_button.setBackground(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.button_pause_riding, null));
                }

                break;
            case R.id.riding_finish_button:
                if (distance / 1000 <= 1.0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("本次运动距离过短,结束将无法保存记录,坚持一下啊")
                            .setPositiveButton("确认退出", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    flagDistance = 0;
                                    handler.removeMessages(Constant.DISTANCE_MSG);
                                    handler.removeMessages(Constant.TIME_MSG);
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
                    mLocationClient.stop();
                    unRegisterReceiver();
                    MyUser newUser = new MyUser();
                    newUser.setRiding_kilometers(user.getRiding_kilometers() + distance / 1000);
                    newUser.setRiding_calorie(user.getRiding_calorie() + calorie);
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
        if (isStarted) {
            Toast.makeText(this, "你还未结束运动哦！", Toast.LENGTH_SHORT).show();
        } else {
            mLocationClient.stop();
            super.onBackPressed();

        }
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
