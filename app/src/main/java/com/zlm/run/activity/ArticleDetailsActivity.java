package com.zlm.run.activity;

import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zlm.run.R;
import com.zlm.run.entity.Constant;

import qiu.niorgai.StatusBarCompat;

public class ArticleDetailsActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private ImageView mImageView;
    private TextView mTextView;

    private String name;
    private String content;
    private String imgUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_details);
        StatusBarCompat.translucentStatusBar(this, true);

        Intent intent = getIntent();
        name = intent.getStringExtra(Constant.INTENT_ARTICLE_NAME);
        content = intent.getStringExtra(Constant.INTENT_ARTICLE_CONTENT);
        imgUrl = intent.getStringExtra(Constant.INTENT_ARTICLE_URL);
        initView();
    }

    private void initView() {
        mToolbar = findViewById(R.id.tb_detail);
        mCollapsingToolbarLayout = findViewById(R.id.ctl_detail);
        mImageView = findViewById(R.id.iv_detail);
        mTextView = findViewById(R.id.tv_detail);

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mCollapsingToolbarLayout.setTitle(name);
        Glide.with(this).load(imgUrl).into(mImageView);
        mTextView.setText(content);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
