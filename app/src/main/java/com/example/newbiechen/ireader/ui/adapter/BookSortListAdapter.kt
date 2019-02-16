package com.example.newbiechen.ireader.ui.adapter

import android.content.Context

import com.example.newbiechen.ireader.model.bean.SortBookBean
import com.example.newbiechen.ireader.ui.adapter.view.BookSortListHolder
import com.example.newbiechen.ireader.ui.base.adapter.IViewHolder
import com.example.newbiechen.ireader.widget.adapter.WholeAdapter

/**
 * Created by newbiechen on 17-5-3.
 */

class BookSortListAdapter(context: Context, options: WholeAdapter.Options) : WholeAdapter<SortBookBean>(context, options) {

    override fun createViewHolder(viewType: Int): IViewHolder<SortBookBean> {
        return BookSortListHolder()
    }
}
