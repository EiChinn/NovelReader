package com.example.newbiechen.ireader.ui.adapter

import com.example.newbiechen.ireader.model.bean.CommentBean
import com.example.newbiechen.ireader.ui.adapter.view.CommentHolder
import com.example.newbiechen.ireader.ui.base.adapter.BaseListAdapter
import com.example.newbiechen.ireader.ui.base.adapter.IViewHolder

/**
 * Created by newbiechen on 17-4-29.
 */

class GodCommentAdapter : BaseListAdapter<CommentBean>() {
    override fun createViewHolder(viewType: Int): IViewHolder<CommentBean> {
        return CommentHolder(true)
    }
}
