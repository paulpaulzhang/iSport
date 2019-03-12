package com.zlm.run.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;
import com.zlm.run.R;
import com.zlm.run.bmob.MyUser;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import qiu.niorgai.StatusBarCompat;

public class ForgetPasswordActivity extends BaseActivity {
    private AVLoadingIndicatorView forget_dialog_loading;
    private EditText forget_et_username;
    private EditText forget_et_password;
    private EditText forget_et_new_password;
    private Button forget_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        StatusBarCompat.translucentStatusBar(this, true);
        initView();
    }

    private void initView() {
        forget_dialog_loading = findViewById(R.id.forget_dialog_loading);
        forget_et_username = findViewById(R.id.forget_et_username);
        forget_et_password = findViewById(R.id.forget_et_password);
        forget_et_new_password = findViewById(R.id.forget_et_new_password);
        forget_button = findViewById(R.id.forget_button);

        forget_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.forget_button:
                changePassword();
                break;
            default:
        }
    }

    /**
     * 当前没有绑定邮箱账号或手机号，修改密码功能仅限已登录用户
     */
    private void changePassword() {

        String username = forget_et_username.getText().toString().trim();
        String password = forget_et_password.getText().toString().trim();
        String newPassword = forget_et_new_password.getText().toString().trim();
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(newPassword)) {
            if (username.equals(BmobUser.getCurrentUser(MyUser.class).getUsername()) && password.equals(BmobUser.getCurrentUser(MyUser.class))) {
                forget_dialog_loading.setVisibility(View.VISIBLE);
                BmobUser.updateCurrentUserPassword(password, newPassword, new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        forget_dialog_loading.setVisibility(View.INVISIBLE);
                        if (e == null) {
                            Toast.makeText(ForgetPasswordActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(ForgetPasswordActivity.this, "修改失败，请重试", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } else {
            Toast.makeText(this, "输入框不能为空", Toast.LENGTH_SHORT).show();
        }

    }
}
