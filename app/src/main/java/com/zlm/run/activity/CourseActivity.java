package com.zlm.run.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.zlm.run.R;
import com.zlm.run.tool.ResourcesUtils;

import qiu.niorgai.StatusBarCompat;

public class CourseActivity extends BaseActivity {
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        StatusBarCompat.translucentStatusBar(this, true);

        initView();

    }

    private void initView() {
        mToolbar = findViewById(R.id.tb_course);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        findViewById(R.id.cv_hot).setOnClickListener(this);
        findViewById(R.id.cv_yoga).setOnClickListener(this);
        findViewById(R.id.cv_basketball).setOnClickListener(this);
        findViewById(R.id.cv_dumbbell).setOnClickListener(this);
        findViewById(R.id.cv_taekwondo).setOnClickListener(this);
        findViewById(R.id.cv_run).setOnClickListener(this);
        findViewById(R.id.cv_body).setOnClickListener(this);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cv_hot:
                startActivity(new Intent(this, HotSportsActivity.class));
                break;
            case R.id.cv_yoga:
                startActivity(new Intent(this, YogaSportsActivity.class));
                break;
            case R.id.cv_basketball:
                startActivity(new Intent(this, BasketballSportsActivity.class));
                break;
            case R.id.cv_dumbbell:
                startActivity(new Intent(this, DumbbellSportsActivity.class));
                break;
            case R.id.cv_taekwondo:
                startActivity(new Intent(this, TaekwondoSportsActivity.class));
                break;
            case R.id.cv_run:
                startActivity(new Intent(this, RunSportsActivity.class));
                break;
            case R.id.cv_body:
                startActivity(new Intent(this, BodySportsActivity.class));
                break;
            default:
        }
    }
}
