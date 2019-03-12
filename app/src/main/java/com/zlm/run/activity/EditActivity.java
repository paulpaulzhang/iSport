package com.zlm.run.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.zlm.run.R;
import com.zlm.run.tool.ShareUtils;

public class EditActivity extends AppCompatActivity {
    private EditText mEditText;
    private Toolbar mToolbar;
    private String content;
    private int num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Intent intent = getIntent();
        content = intent.getStringExtra("edit_content");
        num = intent.getIntExtra("edit_num", 10086);

        mToolbar = findViewById(R.id.tb_edit);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mEditText = findViewById(R.id.et_edit);
        mEditText.setText(content);

    }

    private void saveData() {
        String data = mEditText.getText().toString();
        if (num == 0) {
            ShareUtils.putString(this, "yellow_content", data);
        } else if (num == 1) {
            ShareUtils.putString(this, "red_content", data);
        } else if (num == 2) {
            ShareUtils.putString(this, "blue_content", data);
        } else if (num == 3) {
            ShareUtils.putString(this, "gray_content", data);
        } else {
            Toast.makeText(this, "数据保存失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            case R.id.save:
                saveData();
                finish();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveData();
    }
}
