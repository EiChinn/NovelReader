package com.example.newbiechen.ireader.ui.adapter

import com.example.newbiechen.ireader.model.bean.CollBookBean
import com.example.newbiechen.ireader.ui.adapter.view.CollBookHolder
import com.example.newbiechen.ireader.ui.base.adapter.IViewHolder
import com.example.newbiechen.ireader.widget.adapter.WholeAdapter

/**
 * Created by newbiechen on 17-5-8.
 */

class CollBookAdapter : WholeAdapter<CollBookBean>() {

    override fun createViewHolder(viewType: Int): IViewHolder<CollBookBean> {
        return CollBookHolder()
    }

}
