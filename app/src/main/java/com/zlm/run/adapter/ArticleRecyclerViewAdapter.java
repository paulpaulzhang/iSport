package com.zlm.run.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zlm.run.R;
import com.zlm.run.bmob.Article;

import java.util.List;

/**
 * 项目名：   Run
 * 包名：     com.zlm.run.adapter
 * 文件名：   ArticleRecyclerViewAdapter
 * 创建者：   PaulZhang
 * 创建时间： 2018/9/15 22:08
 * 描述：     TODO
 */
public class ArticleRecyclerViewAdapter extends BaseQuickAdapter<Article, BaseViewHolder> {
    public ArticleRecyclerViewAdapter(int layoutResId, @Nullable List<Article> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Article item) {
        helper.setText(R.id.tv_article_title, item.getName());
        helper.setText(R.id.tv_article_content, item.getContent());
        Glide.with(mContext).load(item.getImage().getFileUrl()).into((ImageView) helper.getView(R.id.iv_article_img));

    }

}
