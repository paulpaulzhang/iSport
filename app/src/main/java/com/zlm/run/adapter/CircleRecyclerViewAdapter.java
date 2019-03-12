package com.zlm.run.adapter;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ctetin.expandabletextviewlibrary.ExpandableTextView;
import com.zlm.run.R;
import com.zlm.run.activity.WebActivity;
import com.zlm.run.bmob.MyUser;
import com.zlm.run.bmob.Post;
import com.zlm.run.tool.CommonUtil;
import com.zlm.run.entity.Constant;
import com.zlm.run.tool.LogUtil;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 项目名：   YourFate
 * 包名：     com.zlm.yourfate.adapter
 * 文件名：   CircleRecyclerViewAdapter
 * 创建者：   PaulZhang
 * 创建时间： 2018/7/9 9:08
 * 描述：     TODO
 */
public class CircleRecyclerViewAdapter extends BaseQuickAdapter<Post, BaseViewHolder> {

    public CircleRecyclerViewAdapter(int layoutResId, @Nullable List<Post> data) {
        super(layoutResId, data);
    }

    @Deprecated
    @Override
    protected void convert(BaseViewHolder helper, final Post item) {
        ExpandableTextView content = helper.getView(R.id.content);
        final TextView author = helper.getView(R.id.author);
        TextView time = helper.getView(R.id.time);
        final CircleImageView author_avatar = helper.getView(R.id.author_avatar);
        final ImageView content_image = helper.getView(R.id.content_image);

        BmobQuery<MyUser> query = new BmobQuery<>();
        query.addWhereEqualTo("objectId", item.getAuthor().getObjectId());
        query.findObjects(new FindListener<MyUser>() {
            @Override
            public void done(List<MyUser> object, BmobException e) {
                if (e == null) {
                    LogUtil.i("查询用户成功:" + object.get(0));
                    author.setText(object.get(0).getNickName());
                    if (object.get(0).getAvatar() != null) {
                        Glide.with(mContext).load(object.get(0).getAvatar().getFileUrl()).into(author_avatar);
                    } else {
                        Glide.with(mContext).load(Constant.AVATAR_URL).into(author_avatar);
                    }
                } else {
                    LogUtil.i("查询用户信息失败:" + e.getMessage());
                    Glide.with(mContext).load(Constant.AVATAR_URL).into(author_avatar);
                }
            }
        });
        String text = item.getContent();
        content.setContent(text);
        content.setLinkClickListener(new ExpandableTextView.OnLinkClickListener() {
            @Override
            public void onLinkClickListener(ExpandableTextView.LinkType linkType, String s) {
                if (linkType == ExpandableTextView.LinkType.LINK_TYPE) {
                    Intent intent = new Intent("com.zlm.run.ACTION_WEB");
                    intent.putExtra(Constant.INTENT_WEB_URL, s);
                    mContext.startActivity(intent);

                } else if (linkType == ExpandableTextView.LinkType.MENTION_TYPE) {
                    Toast.makeText(mContext, "你点击了@用户 内容是：" + s, Toast.LENGTH_SHORT).show();
                }
            }
        });
        time.setText(item.getCreatedAt());
        if (item.getImage() != null) {
            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.place_holder)
                    .skipMemoryCache(true);
            Glide.with(mContext)
                    .load(item.getImage().getFileUrl())
                    .apply(options)
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            int imgWidth = resource.getIntrinsicWidth();
                            int imgHeight = resource.getIntrinsicHeight();
                            float scale = (float) imgHeight / imgWidth;

                            //动态设置imageView的宽高
                            ViewGroup.LayoutParams params = content_image.getLayoutParams();
                            if (scale > 1.7) {
                                params.height = CommonUtil.dp2px(mContext, 200);
                                params.width = CommonUtil.dp2px(mContext, 120);
                            } else if (scale > 1.2 && scale <= 1.7) {
                                params.height = CommonUtil.dp2px(mContext, 200);
                                params.width = CommonUtil.dp2px(mContext, 150);
                            } else if (scale < 1.2 && scale >= 0.8) {
                                params.height = CommonUtil.dp2px(mContext, 100);
                                params.width = CommonUtil.dp2px(mContext, 100);
                            } else if (scale < 0.8 && scale >= 0.7) {
                                params.height = CommonUtil.dp2px(mContext, 150);
                                params.width = CommonUtil.dp2px(mContext, 200);
                            } else if (scale < 0.7 && scale >= 0.5) {
                                params.height = CommonUtil.dp2px(mContext, 120);
                                params.width = CommonUtil.dp2px(mContext, 200);
                            } else if (scale < 0.5) {
                                params.height = CommonUtil.dp2px(mContext, 80);
                                params.width = CommonUtil.dp2px(mContext, 200);
                            } else {
                                params.height = CommonUtil.dp2px(mContext, 125);
                                params.width = CommonUtil.dp2px(mContext, 125);
                            }

                            content_image.setImageDrawable(resource);
                            LogUtil.d(item.getImage().getFileUrl());
                        }
                    });
            helper.addOnClickListener(R.id.content_image);
        }

    }
}
