package com.example.newbiechen.ireader.ui.adapter

import android.content.Context

import com.example.newbiechen.ireader.model.bean.BookReviewBean
import com.example.newbiechen.ireader.ui.adapter.view.DiscReviewHolder
import com.example.newbiechen.ireader.ui.base.adapter.IViewHolder
import com.example.newbiechen.ireader.widget.adapter.WholeAdapter

/**
 * Created by newbiechen on 17-4-21.
 */

class DiscReviewAdapter(context: Context, options: WholeAdapter.Options) : WholeAdapter<BookReviewBean>(context, options) {

    override fun createViewHolder(viewType: Int): IViewHolder<BookReviewBean> {
        return DiscReviewHolder()
    }
}
