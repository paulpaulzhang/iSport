package com.zlm.run.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.yalantis.ucrop.UCrop;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.internal.entity.CaptureStrategy;
import com.zlm.run.R;
import com.zlm.run.bmob.MyUser;
import com.zlm.run.entity.Constant;
import com.zlm.run.tool.Glide4Engine;
import com.zlm.run.tool.ImagePathUtil;
import com.zlm.run.tool.LogUtil;
import com.zlm.run.tool.NumberPickerUtil;
import com.zlm.run.view.CustomDialog;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Objects;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class UserDataActivity extends BaseActivity {
    private CircleImageView data_img;
    private TextView data_nickname;
    private TextView data_birthday;
    private TextView data_gender;
    private TextView data_talking;
    private TextView data_username;
    private TextView data_weight;
    private TextView data_height;
    private LinearLayout data_nickname_layout;
    private LinearLayout data_birthday_layout;
    private LinearLayout data_gender_layout;
    private LinearLayout data_talking_layout;
    private LinearLayout data_weight_layout;
    private LinearLayout data_height_layout;
    private LinearLayout data_avatar;
    private CustomDialog dialog;
    private CustomDialog loading_dialog;
    private Button dialog_btn_camera;
    private Button dialog_btn_album;
    private Button dialog_btn_cancel;
    private Toolbar data_tool_bar;

    private Uri imgUri;
    private String imgPath;

    private Integer targetWeight;
    private Integer targetHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);
        data_tool_bar = findViewById(R.id.data_tool_bar);
        setSupportActionBar(data_tool_bar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initView();
    }

    private void initView() {
        //自定义dialog
        dialog = new CustomDialog(this, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT, R.layout.dialog_image, R.style.Dialog_Theme, Gravity.BOTTOM);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        loading_dialog = new CustomDialog(this, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT, R.layout.dialog_loading, R.style.Dialog_Theme, Gravity.CENTER, R.style.loading_pop_anim_style);
        loading_dialog.setCancelable(false);

        data_img = findViewById(R.id.data_img);
        data_nickname = findViewById(R.id.data_nickname);
        data_birthday = findViewById(R.id.data_birthday);
        data_gender = findViewById(R.id.data_gender);
        data_talking = findViewById(R.id.data_talking);
        data_username = findViewById(R.id.data_username);
        data_avatar = findViewById(R.id.data_avatar);
        data_height = findViewById(R.id.data_height);
        data_weight = findViewById(R.id.data_weight);
        dialog_btn_camera = dialog.findViewById(R.id.dialog_btn_camera);
        dialog_btn_album = dialog.findViewById(R.id.dialog_btn_album);
        dialog_btn_cancel = dialog.findViewById(R.id.dialog_btn_cancel);
        data_nickname_layout = findViewById(R.id.data_nickname_layout);
        data_birthday_layout = findViewById(R.id.data_birthday_layout);
        data_gender_layout = findViewById(R.id.data_gender_layout);
        data_talking_layout = findViewById(R.id.data_talking_layout);
        data_weight_layout = findViewById(R.id.data_weight_layout);
        data_height_layout = findViewById(R.id.data_height_layout);

        data_avatar.setOnClickListener(this);
        dialog_btn_camera.setOnClickListener(this);
        dialog_btn_album.setOnClickListener(this);
        dialog_btn_cancel.setOnClickListener(this);
        data_nickname_layout.setOnClickListener(this);
        data_birthday_layout.setOnClickListener(this);
        data_gender_layout.setOnClickListener(this);
        data_talking_layout.setOnClickListener(this);
        data_height_layout.setOnClickListener(this);
        data_weight_layout.setOnClickListener(this);

        initUserData();
    }

    @SuppressLint("SetTextI18n")
    private void initUserData() {
        MyUser user = BmobUser.getCurrentUser(MyUser.class);
        if (user.getAvatar() != null) {
            Glide.with(this).load(user.getAvatar().getFileUrl()).into(data_img);
        }

        data_username.setText(user.getUsername());

        data_birthday.setText(user.getBirthday());

        data_gender.setText(user.getGender());

        data_nickname.setText(user.getNickName());

        data_talking.setText(user.getTalking());

        data_weight.setText(user.getWeight() + "kg");

        data_height.setText(user.getHeight() + "cm");

        targetHeight = user.getHeight();
        targetWeight = user.getWeight();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.data_avatar:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    openAlbum();
                }
                break;
            case R.id.data_nickname_layout:
                changeNickname();
                break;
            case R.id.data_birthday_layout:
                chooseBirthday();
                break;
            case R.id.data_gender_layout:
                chooseGender();
                break;
            case R.id.data_talking_layout:
                changeTalking();
                break;
            case R.id.data_weight_layout:
                changeWeight();
                break;
            case R.id.data_height_layout:
                changeHeight();
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constant.RESULT_IMAGE:
                if (resultCode == RESULT_OK && data != null) {
                    cropImage(Matisse.obtainResult(data).get(0));
                }
                break;
            case UCrop.REQUEST_CROP:
                if (resultCode == RESULT_OK) {
                    final Uri resultUri = UCrop.getOutput(data);
                    compressPhoto(resultUri);
                }
                break;
            case UCrop.RESULT_ERROR:
                Toast.makeText(this, "无法裁剪图片", Toast.LENGTH_SHORT).show();
                break;
            default:
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "请授予权限", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    //Matisse
    private void openAlbum() {
        Matisse.from(this)
                .choose(MimeType.ofImage())
                .countable(true)
                .capture(true)
                .captureStrategy(new CaptureStrategy(true, "com.zlm.run.fileprovider"))
                .gridExpectedSize(getResources()
                        .getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .thumbnailScale(0.85f)
                .theme(R.style.Matisse_Zhihu_Custom)
                .imageEngine(new Glide4Engine())
                .forResult(Constant.RESULT_IMAGE);
    }

    //压缩图片
    private void compressPhoto(Uri uri) {
        Luban.with(this)
                .load(uri)
                .ignoreBy(0)
                .setTargetDir(Objects.requireNonNull(getExternalCacheDir()).getAbsolutePath())
                .filter(new CompressionPredicate() {
                    @Override
                    public boolean apply(String path) {
                        return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
                    }
                })
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        loading_dialog.show();
                    }

                    @Override
                    public void onSuccess(File file) {

                        imgUri = Uri.fromFile(file);
                        imgPath = ImagePathUtil.getImageAbsolutePath(UserDataActivity.this, imgUri);
                        RequestOptions options = new RequestOptions()
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE);
                        Glide.with(UserDataActivity.this).load(imgPath).apply(options).into(data_img);
                        updateImage();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(UserDataActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                }).launch();
    }

    //上传图片
    private void updateImage() {
        final BmobFile img = new BmobFile(new File(imgPath));
        img.upload(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    MyUser user = BmobUser.getCurrentUser(MyUser.class);
                    user.setAvatar(img);
                    user.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            loading_dialog.dismiss();
                        }
                    });
                }
            }
        });
    }

    //裁剪图片
    private void cropImage(Uri uri) {
        //裁剪后图片保存在文件夹中
        Uri destinationUri = Uri.fromFile(new File(getExternalCacheDir(), System.currentTimeMillis() + ".jgp"));
        UCrop.Options options = new UCrop.Options();
        options.setToolbarColor(Color.parseColor("#000000")); // 设置标题栏颜色
        options.setStatusBarColor(Color.parseColor("#000000")); //设置状态栏颜色
        UCrop.of(uri, destinationUri)
                .withAspectRatio(1, 1)
                .withMaxResultSize(320, 320)
                .withOptions(options)
                .start(this);
    }

    //修改性别
    private void chooseGender() {
        final String[] genders = {"男", "女"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setSingleChoiceItems(genders, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String gender = genders[i];
                data_gender.setText(gender);
                MyUser user = new MyUser();
                user.setGender(gender);
                BmobUser bmobUser = BmobUser.getCurrentUser();
                user.update(bmobUser.getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e != null) {
                            Toast.makeText(UserDataActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialogInterface.dismiss();
            }
        }).show();
    }

    //修改生日
    private void chooseBirthday() {
        Calendar nowDate = Calendar.getInstance();
        int mYear = nowDate.get(Calendar.YEAR);
        int mMonth = nowDate.get(Calendar.MONTH);
        int mDay = nowDate.get(Calendar.DATE);

        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                StringBuffer birthday = new StringBuffer().append(i).append("-").append(i1 + 1).append("-").append(i2);
                data_birthday.setText(birthday);
                MyUser user = new MyUser();
                user.setBirthday(birthday.toString());
                BmobUser bmobUser = BmobUser.getCurrentUser();
                user.update(bmobUser.getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e != null) {
                            LogUtil.d(e.getMessage());
                        }
                    }
                });
            }
        }, mYear, mMonth, mDay).show();
    }

    //修改昵称
    private void changeNickname() {
        // 使用LayoutInflater来加载dialog_edit.xml布局
        View editView = LayoutInflater.from(this).inflate(R.layout.dialog_edit, null);
        View customerTitleView = LayoutInflater.from(this).inflate(R.layout.dialog_customer_title_nick, null);
        final EditText dialog_edit = editView.findViewById(R.id.dialog_edit);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(editView);
        builder.setCustomTitle(customerTitleView)
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String nickname = dialog_edit.getText().toString().trim();
                        // 获取editText的内容,显示到textView
                        data_nickname.setText(nickname);
                        MyUser user = new MyUser();
                        user.setNickName(nickname);
                        BmobUser bmobUser = BmobUser.getCurrentUser();
                        user.update(bmobUser.getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e != null) {
                                    LogUtil.d(e.getMessage());
                                }
                            }
                        });
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //修改签名
    private void changeTalking() {
        // 使用LayoutInflater来加载dialog_edit.xml布局
        View editView = LayoutInflater.from(this).inflate(R.layout.dialog_edit, null);
        View customerTitleView = LayoutInflater.from(this).inflate(R.layout.dialog_customer_title_talking, null);
        final EditText dialog_edit = editView.findViewById(R.id.dialog_edit);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(editView);
        builder.setCustomTitle(customerTitleView)
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String talking = dialog_edit.getText().toString().trim();
                        // 获取editText的内容,显示到textView
                        data_talking.setText(talking);
                        MyUser user = new MyUser();
                        user.setTalking(talking);
                        BmobUser bmobUser = BmobUser.getCurrentUser();
                        user.update(bmobUser.getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e != null) {
                                    LogUtil.d(e.getMessage());
                                }
                            }
                        });
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //修改体重
    private void changeWeight() {
        View pickerView = LayoutInflater.from(this).inflate(R.layout.dialog_number_picker, null);
        View customerTitleView = LayoutInflater.from(this).inflate(R.layout.dialog_customer_title_weight, null);
        NumberPicker mNumberPicker = pickerView.findViewById(R.id.dialog_np);
        mNumberPicker.setMinValue(30);
        mNumberPicker.setMaxValue(150);
        mNumberPicker.setValue(targetWeight);
        NumberPickerUtil.setNumberPickerDividerColor(mNumberPicker, R.color.colorAccent, this);
        mNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                targetWeight = i1;
            }
        });

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(Objects.requireNonNull(this));
        builder.setView(pickerView)
                .setCustomTitle(customerTitleView)
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MyUser user = new MyUser();
                        user.setWeight(targetWeight);
                        BmobUser bmobUser = BmobUser.getCurrentUser();
                        user.update(bmobUser.getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e != null) {
                                    LogUtil.d(e.getMessage());
                                }
                                if (e == null) {
                                    LogUtil.i("weight修改成功");
                                }

                            }
                        });
                        data_weight.setText(targetWeight + "kg");
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    //修改身高
    private void changeHeight() {
        View pickerView = LayoutInflater.from(this).inflate(R.layout.dialog_number_picker, null);
        View customerTitleView = LayoutInflater.from(this).inflate(R.layout.dialog_customer_title_height, null);
        NumberPicker mNumberPicker = pickerView.findViewById(R.id.dialog_np);
        mNumberPicker.setMinValue(100);
        mNumberPicker.setMaxValue(200);
        mNumberPicker.setValue(targetHeight);
        NumberPickerUtil.setNumberPickerDividerColor(mNumberPicker, R.color.colorAccent, this);
        mNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                targetHeight = i1;
            }
        });

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(Objects.requireNonNull(this));
        builder.setView(pickerView)
                .setCustomTitle(customerTitleView)
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MyUser user = new MyUser();
                        user.setHeight(targetHeight);
                        BmobUser bmobUser = BmobUser.getCurrentUser();
                        user.update(bmobUser.getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e != null) {
                                    LogUtil.d(e.getMessage());
                                }
                                if (e == null) {
                                    LogUtil.i("height修改成功");
                                }

                            }
                        });
                        data_height.setText(targetHeight + "cm");
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

}
