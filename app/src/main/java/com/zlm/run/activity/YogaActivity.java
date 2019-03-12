package com.zlm.run.activity;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.zlm.run.R;
import com.zlm.run.bmob.MyUser;
import com.zlm.run.entity.Constant;
import com.zlm.run.tool.LogUtil;
import com.zlm.run.view.CustomDialog;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import qiu.niorgai.StatusBarCompat;

public class YogaActivity extends BaseActivity {
    private int minute = 0;
    private int second = 0;
    private TextView tv_minute;
    private TextView tv_second;
    private ImageButton ib_start;
    private ImageButton ib_pause;
    private ImageButton ib_back;
    private CustomDialog saving_dialog;
    private boolean isFinished = false;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.YOGA_TIME:
                    if (second < 59) {
                        if (second < 9) {
                            tv_second.setText("0" + ++second);
                        } else {
                            tv_second.setText(++second + "");
                        }
                    } else {
                        second = 0;
                        tv_second.setText("00");
                        if (minute < 9) {
                            tv_minute.setText("0" + ++minute);
                        } else {
                            tv_minute.setText(++minute + "");
                        }
                    }
                    handler.sendEmptyMessageDelayed(Constant.YOGA_TIME, 1000);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yoga);
        StatusBarCompat.translucentStatusBar(this, true);

        initView();
    }

    private void initView() {
        tv_minute = findViewById(R.id.tv_minute);
        tv_second = findViewById(R.id.tv_second);
        ib_pause = findViewById(R.id.ib_pause);
        ib_start = findViewById(R.id.ib_start);
        ib_back = findViewById(R.id.ib_back);
        ib_start.setOnClickListener(this);
        ib_pause.setOnClickListener(this);
        ib_back.setOnClickListener(this);

        saving_dialog = new CustomDialog(this, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT, R.layout.dialog_saving, R.style.Dialog_Theme, Gravity.CENTER, R.style.pop_anim_style);
        saving_dialog.setCancelable(false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_pause:
                ib_pause.setVisibility(View.GONE);
                ib_start.setVisibility(View.VISIBLE);
                handler.removeMessages(Constant.YOGA_TIME);
                break;
            case R.id.ib_start:
                ib_start.setVisibility(View.GONE);
                ib_pause.setVisibility(View.VISIBLE);
                handler.sendEmptyMessageDelayed(Constant.YOGA_TIME, 1000);
                break;
            case R.id.ib_back:
                saving_dialog.show();
                MyUser user = BmobUser.getCurrentUser(MyUser.class);
                MyUser newUser = new MyUser();
                newUser.setYoga_time(user.getYoga_time() + minute);
                newUser.update(user.getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        saving_dialog.dismiss();
                        isFinished = true;
                        if (e == null) {
                            LogUtil.d("更新用户信息成功");
                            Toast.makeText(YogaActivity.this, "数据上传成功", Toast.LENGTH_SHORT).show();

                        } else {
                            LogUtil.d("更新用户信息失败:" + e.getMessage());
                        }
                        finish();
                    }
                });
                break;
            default:
        }
    }

    @Override
    public void onBackPressed() {
        if (isFinished) {
            finish();
        } else {
            saving_dialog.show();
            MyUser user = BmobUser.getCurrentUser(MyUser.class);
            MyUser newUser = new MyUser();
            newUser.setYoga_time(user.getYoga_time() + minute);
            newUser.update(user.getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    saving_dialog.dismiss();
                    isFinished = true;
                    if (e == null) {
                        LogUtil.d("更新用户信息成功");
                        Toast.makeText(YogaActivity.this, "数据上传成功", Toast.LENGTH_SHORT).show();

                    } else {
                        LogUtil.d("更新用户信息失败:" + e.getMessage());
                    }
                    finish();
                }
            });

        }
    }
}
