package com.zlm.run.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.zlm.run.R;
import com.zlm.run.entity.Constant;

public class ImageActivity extends Activity {
    private String imgUrl;
    private PhotoView photoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        photoView = findViewById(R.id.photo_view);
        photoView.enable();
        Intent intent = getIntent();
        imgUrl = intent.getStringExtra(Constant.INTENT_POST_URL);
        Glide.with(this).load(imgUrl).into(photoView);

        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
