package com.example.newbiechen.ireader.ui.adapter

import android.content.Context

import com.example.newbiechen.ireader.model.bean.DetailBookBean
import com.example.newbiechen.ireader.ui.adapter.view.BookListInfoHolder
import com.example.newbiechen.ireader.ui.base.adapter.IViewHolder
import com.example.newbiechen.ireader.widget.adapter.WholeAdapter

/**
 * Created by newbiechen on 17-5-2.
 */

class BookListDetailAdapter(context: Context, options: WholeAdapter.Options) : WholeAdapter<DetailBookBean>(context, options) {

    override fun createViewHolder(viewType: Int): IViewHolder<DetailBookBean> {
        return BookListInfoHolder()
    }
}
