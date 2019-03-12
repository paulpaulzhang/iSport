package com.zlm.run.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zlm.run.R;
import com.zlm.run.tool.ShareUtils;

import qiu.niorgai.StatusBarCompat;

public class AssignmentActivity extends BaseActivity {
    private TextView tv_red;
    private TextView tv_yellow;
    private TextView tv_blue;
    private TextView tv_gray;
    private ImageButton ib_back;

    private String yellow;
    private String red;
    private String blue;
    private String gray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment);

        StatusBarCompat.translucentStatusBar(this, true);

        initView();
    }

    private void initView() {
        tv_red = findViewById(R.id.tv_green);
        tv_yellow = findViewById(R.id.tv_red);
        tv_blue = findViewById(R.id.tv_yellow);
        tv_gray = findViewById(R.id.tv_blue);
        ib_back = findViewById(R.id.ib_back);
        tv_yellow.setOnClickListener(this);
        tv_red.setOnClickListener(this);
        tv_blue.setOnClickListener(this);
        tv_gray.setOnClickListener(this);
        ib_back.setOnClickListener(this);
        refreshData();
    }

    private void refreshData() {
        yellow = ShareUtils.getString(this, "yellow_content", "");
        red = ShareUtils.getString(this, "red_content", "");
        blue = ShareUtils.getString(this, "blue_content", "");
        gray = ShareUtils.getString(this, "gray_content", "");

        tv_red.setText(red);
        tv_yellow.setText(yellow);
        tv_blue.setText(blue);
        tv_gray.setText(gray);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_red:
                Intent redIntent = new Intent(this, EditActivity.class);
                redIntent.putExtra("edit_content", yellow);
                redIntent.putExtra("edit_num", 0);
                startActivity(redIntent);
                break;
            case R.id.tv_green:
                Intent greenIntent = new Intent(this, EditActivity.class);
                greenIntent.putExtra("edit_content", red);
                greenIntent.putExtra("edit_num", 1);
                startActivity(greenIntent);
                break;
            case R.id.tv_yellow:
                Intent yellowIntent = new Intent(this, EditActivity.class);
                yellowIntent.putExtra("edit_content", blue);
                yellowIntent.putExtra("edit_num", 2);
                startActivity(yellowIntent);
                break;
            case R.id.tv_blue:
                Intent blueIntent = new Intent(this, EditActivity.class);
                blueIntent.putExtra("edit_content", gray);
                blueIntent.putExtra("edit_num", 3);
                startActivity(blueIntent);
                break;
            case R.id.ib_back:
                finish();
                break;
            default:
        }
    }

    @Override
    protected void onResume() {
        refreshData();
        super.onResume();

    }
}
