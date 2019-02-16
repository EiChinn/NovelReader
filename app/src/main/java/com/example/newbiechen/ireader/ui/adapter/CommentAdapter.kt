package com.example.newbiechen.ireader.ui.adapter

import android.content.Context

import com.example.newbiechen.ireader.model.bean.CommentBean
import com.example.newbiechen.ireader.ui.adapter.view.CommentHolder
import com.example.newbiechen.ireader.ui.base.adapter.IViewHolder
import com.example.newbiechen.ireader.widget.adapter.WholeAdapter

/**
 * Created by newbiechen on 17-4-29.
 */

class CommentAdapter(context: Context, options: WholeAdapter.Options) : WholeAdapter<CommentBean>(context, options) {

    override fun createViewHolder(viewType: Int): IViewHolder<CommentBean> {
        return CommentHolder(false)
    }
}
