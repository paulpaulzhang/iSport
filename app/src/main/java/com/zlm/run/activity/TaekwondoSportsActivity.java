package com.zlm.run.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.zlm.run.R;

public class TaekwondoSportsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taekwondo_sports);

        initView();
    }

    private void initView() {
        Toolbar mToolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        findViewById(R.id.cv_1).setOnClickListener(this);
        findViewById(R.id.cv_2).setOnClickListener(this);
        findViewById(R.id.cv_3).setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cv_1:
                Intent intent1 = new Intent(this, CourseDetailsActivity.class);
                intent1.putExtra("res_id", "tqdfslz");
                startActivity(intent1);
                break;
            case R.id.cv_2:
                Intent intent2 = new Intent(this, CourseDetailsActivity.class);
                intent2.putExtra("res_id", "tqdlxdyj");
                startActivity(intent2);
                break;
            case R.id.cv_3:
                Intent intent3 = new Intent(this, CourseDetailsActivity.class);
                intent3.putExtra("res_id", "tqdjdjs");
                startActivity(intent3);
                break;
            default:
        }
    }
}
