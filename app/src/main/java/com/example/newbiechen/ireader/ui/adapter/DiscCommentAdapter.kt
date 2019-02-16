package com.example.newbiechen.ireader.ui.adapter

import android.content.Context

import com.example.newbiechen.ireader.model.bean.BookCommentBean
import com.example.newbiechen.ireader.ui.adapter.view.DiscCommentHolder
import com.example.newbiechen.ireader.ui.base.adapter.IViewHolder
import com.example.newbiechen.ireader.widget.adapter.WholeAdapter

/**
 * Created by newbiechen on 17-4-20.
 */

class DiscCommentAdapter(context: Context, options: WholeAdapter.Options) : WholeAdapter<BookCommentBean>(context, options) {

    override fun createViewHolder(viewType: Int): IViewHolder<BookCommentBean> {
        return DiscCommentHolder()
    }
}
