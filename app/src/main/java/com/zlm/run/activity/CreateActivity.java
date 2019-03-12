package com.zlm.run.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.wang.avi.AVLoadingIndicatorView;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.internal.entity.CaptureStrategy;
import com.zlm.run.R;
import com.zlm.run.bmob.MyUser;
import com.zlm.run.bmob.Post;
import com.zlm.run.entity.Constant;
import com.zlm.run.tool.Glide4Engine;
import com.zlm.run.tool.ImagePathUtil;
import com.zlm.run.tool.LogUtil;
import com.zlm.run.view.CustomDialog;

import java.io.File;
import java.util.Objects;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class CreateActivity extends BaseActivity {
    private Toolbar create_toolbar;
    private EditText create_et_input;
    private Button create_button;
    private AVLoadingIndicatorView create_dialog_loading;
    private ImageView create_image;
    private CustomDialog loading_dialog;
    private ImageView create_delete_button;

    private Uri imgUri;
    private String imgPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        initView();
    }


    private void initView() {
        create_toolbar = findViewById(R.id.create_toolbar);
        setSupportActionBar(create_toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //自定义dialog
        loading_dialog = new CustomDialog(this, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT, R.layout.dialog_loading, R.style.Dialog_Theme, Gravity.CENTER, R.style.loading_pop_anim_style);
        loading_dialog.setCancelable(false);

        create_dialog_loading = findViewById(R.id.create_dialog_loading);
        create_et_input = findViewById(R.id.create_et_input);
        create_button = findViewById(R.id.create_button);
        create_image = findViewById(R.id.create_image);
        create_delete_button = findViewById(R.id.create_delete_button);
        create_delete_button.setVisibility(View.INVISIBLE);

        create_image.setOnClickListener(this);
        create_button.setOnClickListener(this);
        create_delete_button.setOnClickListener(this);

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
            case R.id.create_button:
                String content = create_et_input.getText().toString();
                if (!TextUtils.isEmpty(content)) {
                    publishData(content);
                } else {
                    Toast.makeText(this, "你什么也没有写哦！", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.create_image:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    openAlbum();
                }
                break;
            case R.id.create_delete_button:
                create_delete_button.setVisibility(View.INVISIBLE);
                create_image.setImageResource(R.drawable.add_photo);
                imgUri = null;
                imgPath = null;
                break;
            default:
        }
    }

    private void publishData(final String content) {
        if (imgPath == null) {
            Toast.makeText(this, "来张图吧", Toast.LENGTH_SHORT).show();
        } else {
            create_dialog_loading.setVisibility(View.VISIBLE);
            final BmobFile imgFile = new BmobFile(new File(imgPath));
            imgFile.upload(new UploadFileListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        MyUser user = BmobUser.getCurrentUser(MyUser.class);
                        Post post = new Post();
                        post.setAuthor(user);
                        post.setImage(imgFile);
                        post.setContent(content);

                        post.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                create_dialog_loading.setVisibility(View.INVISIBLE);
                                if (e == null) {
                                    Toast.makeText(CreateActivity.this, "发表成功", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(CreateActivity.this, "发表失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        LogUtil.i(e.toString());
                    }
                }
            });
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constant.REQUEST_CODE_CHOOSE:
                if (resultCode == RESULT_OK && data != null) {
                    compressPhoto(Matisse.obtainResult(data).get(0));
                    create_delete_button.setVisibility(View.VISIBLE);
                }
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
                .forResult(Constant.REQUEST_CODE_CHOOSE);
    }

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
                        loading_dialog.dismiss();
                        imgUri = Uri.fromFile(file);
                        imgPath = ImagePathUtil.getImageAbsolutePath(CreateActivity.this, imgUri);
                        RequestOptions options = new RequestOptions()
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE);
                        Glide.with(CreateActivity.this).load(imgPath).apply(options).into(create_image);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(CreateActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                }).launch();
    }

}
