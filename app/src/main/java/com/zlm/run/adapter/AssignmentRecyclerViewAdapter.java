package com.zlm.run.adapter;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zlm.run.R;

import java.util.List;

/**
 * 项目名：   Run
 * 包名：     com.zlm.run.adapter
 * 文件名：   AssignmentRecyclerViewAdapter
 * 创建者：   PaulZhang
 * 创建时间： 2018/9/25 23:32
 * 描述：     TODO
 */
public class AssignmentRecyclerViewAdapter extends BaseItemDraggableAdapter<String, BaseViewHolder> {
    public AssignmentRecyclerViewAdapter(int layoutResId, List<String> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tv_item, item);
    }
}
