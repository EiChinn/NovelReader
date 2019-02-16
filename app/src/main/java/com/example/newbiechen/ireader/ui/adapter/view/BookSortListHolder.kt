package com.example.newbiechen.ireader.ui.adapter.view

import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.model.bean.SortBookBean
import com.example.newbiechen.ireader.ui.base.adapter.ViewHolderImpl
import com.example.newbiechen.ireader.utils.Constant

/**
 * Created by newbiechen on 17-5-3.
 */

class BookSortListHolder : ViewHolderImpl<SortBookBean>() {

    private var mIvPortrait: ImageView? = null
    private var mTvTitle: TextView? = null
    private var mTvAuthor: TextView? = null
    private var mTvBrief: TextView? = null
    private var mTvMsg: TextView? = null

    override fun getItemLayoutId(): Int {
        return R.layout.item_book_brief
    }

    override fun initView() {
        mIvPortrait = findById(R.id.book_brief_iv_portrait)
        mTvTitle = findById(R.id.book_brief_tv_title)
        mTvAuthor = findById(R.id.book_brief_tv_author)
        mTvBrief = findById(R.id.book_brief_tv_brief)
        mTvMsg = findById(R.id.book_brief_tv_msg)
    }

    override fun onBind(value: SortBookBean, pos: Int) {
        //头像
        Glide.with(getContext())
                .load(Constant.IMG_BASE_URL + value.cover)
                .placeholder(R.drawable.ic_default_portrait)
                .error(R.drawable.ic_load_error)
                .fitCenter()
                .into(mIvPortrait!!)
        //书单名
        mTvTitle!!.text = value.title
        //作者
        mTvAuthor!!.text = value.author
        //简介
        mTvBrief!!.text = value.shortIntro
        //信息
        mTvMsg!!.text = getContext().resources.getString(R.string.nb_book_message,
                value.latelyFollower, value.retentionRatio)
    }
}
