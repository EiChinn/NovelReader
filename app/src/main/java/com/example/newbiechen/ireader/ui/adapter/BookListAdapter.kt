package com.example.newbiechen.ireader.ui.adapter

import android.content.Context

import com.example.newbiechen.ireader.model.bean.BookListBean
import com.example.newbiechen.ireader.ui.adapter.view.BookListHolder
import com.example.newbiechen.ireader.ui.base.adapter.IViewHolder
import com.example.newbiechen.ireader.widget.adapter.WholeAdapter

/**
 * Created by newbiechen on 17-5-1.
 */

class BookListAdapter : WholeAdapter<BookListBean> {

    constructor(context: Context, options: WholeAdapter.Options?) : super(context, options)

    override fun createViewHolder(viewType: Int): IViewHolder<BookListBean> {
        return BookListHolder()
    }
}
