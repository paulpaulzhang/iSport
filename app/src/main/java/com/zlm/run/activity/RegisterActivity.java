package com.zlm.run.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;
import com.zlm.run.R;
import com.zlm.run.bmob.MyUser;
import com.zlm.run.entity.Constant;
import com.zlm.run.tool.LogUtil;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import qiu.niorgai.StatusBarCompat;

/**
 * 账号注册界面
 **/
public class RegisterActivity extends BaseActivity {
    private Button register_button;
    private EditText register_et_username;
    private EditText register_et_password;
    private EditText register_et_nickname;
    private AVLoadingIndicatorView avLoadingIndicatorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        StatusBarCompat.translucentStatusBar(this, true);

        initView();
    }

    private void initView() {
        register_button = findViewById(R.id.register_button);
        register_et_username = findViewById(R.id.register_et_username);
        register_et_password = findViewById(R.id.register_et_password);
        register_et_nickname = findViewById(R.id.register_et_nickname);
        avLoadingIndicatorView = findViewById(R.id.register_dialog_loading);

        register_button.setOnClickListener(this);
    }

    private void register(String username, String password, String nickname) {
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(nickname)) {
            if (username.matches(Constant.REGEX_USERNAME) && password.matches(Constant.REGEX_USERNAME)) {
                avLoadingIndicatorView.setVisibility(View.VISIBLE);
                MyUser user = new MyUser();
                user.setUsername(username);
                user.setPassword(password);
                user.setNickName(nickname);
                user.setBirthday("1970-1-1");
                user.setGender("男");
                user.setTalking("这个人很懒，什么也没有留下");
                user.setHeight(170);
                user.setWeight(60);
                user.setRunning_kilometers(0.0);
                user.setRunning_calorie(0);
                user.setRiding_calorie(0);
                user.setRiding_kilometers(0.0);
                user.setWalk_calorie(0);
                user.setWalk_kilometers(0.0);
                user.setWalk_number(0);
                user.setYoga_time(0);
                user.setSports_energy(0);
                user.signUp(new SaveListener<MyUser>() {
                    @Override
                    public void done(MyUser myUser, BmobException e) {
                        avLoadingIndicatorView.setVisibility(View.INVISIBLE);
                        if (e == null) {
                            Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "请重试 " + e.toString(), Toast.LENGTH_SHORT).show();
                            LogUtil.d(e.toString());
                        }
                    }
                });
            } else {
                Toast.makeText(this, "用户名由6-16位字母或数字或-_组成\n" +
                        "密码可由数字、大写字母、小写字母组成\n" + "重新输吧", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "输入框不能为空", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_button:
                String username = register_et_username.getText().toString().trim();
                String password = register_et_password.getText().toString();
                String nickname = register_et_nickname.getText().toString().trim();
                register(username, password, nickname);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterActivity.this);
        dialog.setTitle("确认退出");
        dialog.setCancelable(false);
        dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        dialog.show();
    }
}
