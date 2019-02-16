package com.example.newbiechen.ireader.ui.adapter.view

import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.model.bean.packages.SearchBooksBean
import com.example.newbiechen.ireader.ui.base.adapter.ViewHolderImpl
import com.example.newbiechen.ireader.utils.Constant

/**
 * Created by newbiechen on 17-6-2.
 */

class SearchBookHolder : ViewHolderImpl<SearchBooksBean>() {

    private var mIvCover: ImageView? = null
    private var mTvName: TextView? = null
    private var mTvBrief: TextView? = null

    override fun initView() {
        mIvCover = findById(R.id.search_book_iv_cover)
        mTvName = findById(R.id.search_book_tv_name)
        mTvBrief = findById(R.id.search_book_tv_brief)
    }

    override fun onBind(data: SearchBooksBean, pos: Int) {
        //显示图片
        Glide.with(getContext())
                .load(Constant.IMG_BASE_URL + data.cover)
                .placeholder(R.drawable.ic_book_loading)
                .error(R.drawable.ic_load_error)
                .into(mIvCover!!)

        mTvName!!.text = data.title

        mTvBrief!!.text = getContext().getString(R.string.nb_search_book_brief,
                data.latelyFollower, data.retentionRatio, data.author)
    }

    override fun getItemLayoutId(): Int {
        return R.layout.item_search_book
    }
}
