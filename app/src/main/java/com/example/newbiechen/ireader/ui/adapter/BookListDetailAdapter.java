package com.example.newbiechen.ireader.ui.adapter;

import android.content.Context;

import com.example.newbiechen.ireader.model.bean.DetailBookBean;
import com.example.newbiechen.ireader.ui.adapter.view.BookListInfoHolder;
import com.example.newbiechen.ireader.ui.base.adapter.IViewHolder;
import com.example.newbiechen.ireader.widget.adapter.WholeAdapter;

/**
 * Created by newbiechen on 17-5-2.
 */

public class BookListDetailAdapter extends WholeAdapter<DetailBookBean> {
    public BookListDetailAdapter(Context context, Options options) {
        super(context, options);
    }

    @Override
    protected IViewHolder<DetailBookBean> createViewHolder(int viewType) {
        return new BookListInfoHolder();
    }
}
