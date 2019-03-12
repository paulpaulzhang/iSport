package com.zlm.run.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zlm.run.R;
import com.zlm.run.activity.ArticleDetailsActivity;
import com.zlm.run.adapter.ArticleRecyclerViewAdapter;
import com.zlm.run.bmob.Article;
import com.zlm.run.entity.Constant;
import com.zlm.run.tool.LogUtil;
import com.zlm.run.tool.NetUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


public class ArticleFragment extends Fragment {
    private Toolbar mToolbar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ImageView mImageView;
    private RecyclerView mRecyclerView;
    private BaseQuickAdapter mAdapter;
    private List<Article> articles;

    private int curPage = 0;
    private String lastTime = null;
    private int loadLimit = 20;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article, container, false);
        init(view);
        mSwipeRefreshLayout.setRefreshing(true);
        mAdapter.setEnableLoadMore(false);
        updateData(0, Constant.STATE_REFRESH, loadLimit);
        return view;
    }

    private void init(View view) {
        mToolbar = view.findViewById(R.id.tb_article);
        mSwipeRefreshLayout = view.findViewById(R.id.srl_article);
        mImageView = view.findViewById(R.id.iv_article_top);
        mRecyclerView = view.findViewById(R.id.rl_article);

        mAdapter = new ArticleRecyclerViewAdapter(R.layout.article_item, articles);
        articles = new ArrayList<>();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(mToolbar);
        mToolbar.setTitle("资讯");

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter.setEnableLoadMore(false);
                updateData(0, Constant.STATE_REFRESH, loadLimit);
            }
        });

        mAdapter.setPreLoadNumber(5);
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                updateData(curPage, Constant.STATE_MORE, loadLimit);
            }
        }, mRecyclerView);

        mAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mAdapter.isFirstOnly(false);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (articles.size() > 0) {
                    Intent intent = new Intent(getContext(), ArticleDetailsActivity.class);
                    intent.putExtra(Constant.INTENT_ARTICLE_NAME, articles.get(position).getName());
                    intent.putExtra(Constant.INTENT_ARTICLE_URL, articles.get(position).getImage().getFileUrl());
                    intent.putExtra(Constant.INTENT_ARTICLE_CONTENT, articles.get(position).getContent());
                    startActivity(intent);
                }
            }
        });

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.iv_article_top:
                        mRecyclerView.smoothScrollToPosition(0);
                        mSwipeRefreshLayout.setRefreshing(true);
                        mAdapter.setEnableLoadMore(false);
                        updateData(0, Constant.STATE_REFRESH, loadLimit);
                        break;
                }
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (!recyclerView.canScrollVertically(-1)) {
                    mImageView.setVisibility(View.INVISIBLE);
                } else if (!recyclerView.canScrollVertically(1)) {
                    mImageView.setVisibility(View.VISIBLE);
                } else if (dy < 0) {
                    mImageView.setVisibility(View.VISIBLE);
                } else if (dy > 0) {
                    mImageView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    /**
     * 获取更新数据
     *
     * @param page       页数
     * @param actionType 刷新或者加载下一页
     * @param limit      每一页的条数限制
     */
    @SuppressWarnings("unchecked")
    private void updateData(int page, final int actionType, int limit) {
        if (NetUtil.isNetworkAvailable(getContext())) {
            BmobQuery<Article> query = new BmobQuery<>();
            query.order("-createdAt");
            int count = loadLimit;
            // 如果是加载更多
            if (actionType == Constant.STATE_MORE) {
                // 处理时间查询
                Date date = null;
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    date = sdf.parse(lastTime);
                    Log.i("0414", date.toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                // 只查询小于等于最后一个item发表时间的数据
                query.addWhereLessThanOrEqualTo("createdAt", new BmobDate(date));
                // 跳过之前页数并去掉重复数据
                query.setSkip(page * count - count);
            } else {
                // 下拉刷新
                page = 0;
                query.setSkip(page);
            }
            query.setLimit(limit);
            query.findObjects(new FindListener<Article>() {
                @Override
                public void done(List<Article> list, BmobException e) {
                    if (e == null) {
                        if (actionType == Constant.STATE_REFRESH) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                        if (list.size() > 0) {
                            if (actionType == Constant.STATE_REFRESH) {
                                curPage = 0;
                                mAdapter.setNewData(list);
                                articles.clear();
                                lastTime = list.get(list.size() - 1).getCreatedAt();

                            } else {
                                mAdapter.addData(list);
                                mAdapter.loadMoreComplete();
                            }
                            articles.addAll(list);
                            curPage++;
                        } else {
                            mAdapter.loadMoreEnd();
                        }
                    } else {
                        mAdapter.loadMoreFail();
                    }
                }
            });
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
            mAdapter.loadMoreFail();
            View view = View.inflate(getContext(), R.layout.empty_view, null);
            TextView mTextView = view.findViewById(R.id.tv_reload);
            mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSwipeRefreshLayout.setRefreshing(true);
                    mAdapter.setEnableLoadMore(false);
                    updateData(0, Constant.STATE_REFRESH, loadLimit);
                }
            });
            mAdapter.setEmptyView(view);
        }
    }

}
