package com.example.newbiechen.ireader.ui.adapter

import com.example.newbiechen.ireader.model.bean.SectionBean
import com.example.newbiechen.ireader.ui.adapter.view.SectionHolder
import com.example.newbiechen.ireader.ui.base.adapter.BaseListAdapter
import com.example.newbiechen.ireader.ui.base.adapter.IViewHolder

/**
 * Created by newbiechen on 17-4-16.
 */

class SectionAdapter : BaseListAdapter<SectionBean>() {
    override fun createViewHolder(viewType: Int): IViewHolder<SectionBean> {
        return SectionHolder()
    }
}
