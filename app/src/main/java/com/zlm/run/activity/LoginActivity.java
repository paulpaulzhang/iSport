package com.zlm.run.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;
import com.zlm.run.R;
import com.zlm.run.bmob.MyUser;
import com.zlm.run.entity.Constant;
import com.zlm.run.tool.LogUtil;
import com.zlm.run.tool.ShareUtils;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import qiu.niorgai.StatusBarCompat;

/**
 * 登陆界面
 **/
public class LoginActivity extends BaseActivity {
    private EditText login_et_username;
    private EditText login_et_password;
    private Button login_button;
    private TextView login_button_register;
    private TextView login_button_forget;
    private AVLoadingIndicatorView avLoadingIndicatorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        StatusBarCompat.translucentStatusBar(this, true);

        initView();
    }

    private void initView() {
        login_et_username = findViewById(R.id.login_et_username);
        login_et_password = findViewById(R.id.login_et_password);
        login_button = findViewById(R.id.login_button);
        login_button_forget = findViewById(R.id.login_button_forget);
        login_button_register = findViewById(R.id.login_button_register);
        avLoadingIndicatorView = findViewById(R.id.login_dialog_loading);


        login_et_username.setOnClickListener(this);
        login_et_password.setOnClickListener(this);
        login_button.setOnClickListener(this);
        login_button_forget.setOnClickListener(this);
        login_button_register.setOnClickListener(this);

        String username = ShareUtils.getString(this, Constant.USERNAME_KEY, "zlm315zlm");
        String password = ShareUtils.getString(this, Constant.PASSWORD_KEY, "ZLM0315zlm");

        if (!username.equals("")) {
            login_et_username.setText(username);
        }

        if (!password.equals("")) {
            login_et_password.setText(password);
        }


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_button:
                String username = login_et_username.getText().toString().trim();
                String password = login_et_password.getText().toString().trim();
                if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                    avLoadingIndicatorView.setVisibility(View.VISIBLE);
                    login(username, password);
                } else {
                    Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.login_button_forget:
                startActivity(new Intent(this, ForgetPasswordActivity.class));
                break;
            case R.id.login_button_register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            default:
        }
    }

    private void login(final String username, final String password) {
        BmobUser.loginByAccount(username, password, new LogInListener<MyUser>() {
            @Override
            public void done(MyUser myUser, BmobException e) {
                avLoadingIndicatorView.setVisibility(View.INVISIBLE);
                if (e == null) {
                    ShareUtils.putString(LoginActivity.this, Constant.USERNAME_KEY, username);
                    ShareUtils.putString(LoginActivity.this, Constant.PASSWORD_KEY, password);
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "登陆失败", Toast.LENGTH_SHORT).show();
                    LogUtil.d(e.toString());
                }
            }
        });
    }
}