package com.example.newbiechen.ireader.ui.adapter

import com.example.newbiechen.ireader.db.entity.CollBook
import com.example.newbiechen.ireader.ui.adapter.view.CollBookHolder
import com.example.newbiechen.ireader.ui.base.adapter.IViewHolder
import com.example.newbiechen.ireader.widget.adapter.WholeAdapter

/**
 * Created by newbiechen on 17-5-8.
 */

class CollBookAdapter : WholeAdapter<CollBook>() {

    override fun createViewHolder(viewType: Int): IViewHolder<CollBook> {
        return CollBookHolder()
    }

}
