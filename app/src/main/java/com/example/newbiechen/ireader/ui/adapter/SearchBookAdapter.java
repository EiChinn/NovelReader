package com.example.newbiechen.ireader.ui.adapter;

import com.example.newbiechen.ireader.model.bean.packages.SearchBooksBean;
import com.example.newbiechen.ireader.ui.adapter.view.SearchBookHolder;
import com.example.newbiechen.ireader.ui.base.adapter.BaseListAdapter;
import com.example.newbiechen.ireader.ui.base.adapter.IViewHolder;

/**
 * Created by newbiechen on 17-6-2.
 */

public class SearchBookAdapter extends BaseListAdapter<SearchBooksBean>{
    @Override
    protected IViewHolder<SearchBooksBean> createViewHolder(int viewType) {
        return new SearchBookHolder();
    }
}
