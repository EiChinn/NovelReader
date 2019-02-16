package com.example.newbiechen.ireader.ui.adapter

import com.example.newbiechen.ireader.model.bean.BookSortBean
import com.example.newbiechen.ireader.ui.adapter.view.BookSortHolder
import com.example.newbiechen.ireader.ui.base.adapter.BaseListAdapter
import com.example.newbiechen.ireader.ui.base.adapter.IViewHolder

/**
 * Created by newbiechen on 17-4-23.
 */

class BookSortAdapter : BaseListAdapter<BookSortBean>() {

    override fun createViewHolder(viewType: Int): IViewHolder<BookSortBean> {
        return BookSortHolder()
    }
}
