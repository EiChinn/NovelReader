package com.example.newbiechen.ireader.ui.adapter

import com.example.newbiechen.ireader.model.bean.BookSourcesBean
import com.example.newbiechen.ireader.ui.adapter.view.ChangeSourceHolder
import com.example.newbiechen.ireader.ui.base.adapter.IViewHolder
import com.example.newbiechen.ireader.widget.adapter.WholeAdapter

class ChangeSourceAdapter(private val currentSource: String?) : WholeAdapter<BookSourcesBean>() {

    override fun createViewHolder(viewType: Int): IViewHolder<BookSourcesBean> {
        return ChangeSourceHolder(currentSource)
    }

}