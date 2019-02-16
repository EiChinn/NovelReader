package com.example.newbiechen.ireader.ui.adapter

import com.example.newbiechen.ireader.model.bean.HotCommentBean
import com.example.newbiechen.ireader.ui.adapter.view.HotCommentHolder
import com.example.newbiechen.ireader.ui.base.adapter.BaseListAdapter
import com.example.newbiechen.ireader.ui.base.adapter.IViewHolder

/**
 * Created by newbiechen on 17-5-4.
 */

class HotCommentAdapter : BaseListAdapter<HotCommentBean>() {
    override fun createViewHolder(viewType: Int): IViewHolder<HotCommentBean> {
        return HotCommentHolder()
    }
}
