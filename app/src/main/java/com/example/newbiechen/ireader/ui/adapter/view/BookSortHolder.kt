package com.example.newbiechen.ireader.ui.adapter.view

import android.widget.TextView

import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.model.bean.BookSortBean
import com.example.newbiechen.ireader.ui.base.adapter.ViewHolderImpl

/**
 * Created by newbiechen on 17-4-23.
 */

class BookSortHolder : ViewHolderImpl<BookSortBean>() {

    private var mTvType: TextView? = null
    private var mTvCount: TextView? = null

    override fun initView() {
        mTvType = findById(R.id.sort_tv_type)
        mTvCount = findById(R.id.sort_tv_count)
    }

    override fun onBind(value: BookSortBean, pos: Int) {
        mTvType!!.text = value.name
        mTvCount!!.text = getContext().resources.getString(R.string.nb_sort_book_count, value.bookCount)
    }

    override fun getItemLayoutId(): Int {
        return R.layout.item_sort
    }
}
