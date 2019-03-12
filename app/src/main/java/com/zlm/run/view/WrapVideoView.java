package com.zlm.run.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * 项目名：   Run
 * 包名：     com.zlm.run
 * 文件名：   WrapVideoView
 * 创建者：   PaulZhang
 * 创建时间： 2018/7/1 9:56
 * 描述：     TODO
 */
public class WrapVideoView extends VideoView {

    public WrapVideoView(Context context) {
        super(context);
        requestFocus();
    }

    public WrapVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WrapVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(getWidth(), widthMeasureSpec);
        int height = getDefaultSize(getHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
