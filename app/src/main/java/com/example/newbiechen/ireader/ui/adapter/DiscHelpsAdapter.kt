package com.example.newbiechen.ireader.ui.adapter

import android.content.Context

import com.example.newbiechen.ireader.model.bean.BookHelpsBean
import com.example.newbiechen.ireader.ui.adapter.view.DiscHelpsHolder
import com.example.newbiechen.ireader.ui.base.adapter.IViewHolder
import com.example.newbiechen.ireader.widget.adapter.WholeAdapter

/**
 * Created by newbiechen on 17-4-21.
 */

class DiscHelpsAdapter(context: Context, options: WholeAdapter.Options) : WholeAdapter<BookHelpsBean>(context, options) {

    override fun createViewHolder(viewType: Int): IViewHolder<BookHelpsBean> {
        return DiscHelpsHolder()
    }
}
