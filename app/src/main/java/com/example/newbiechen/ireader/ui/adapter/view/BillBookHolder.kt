package com.example.newbiechen.ireader.ui.adapter.view

import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.newbiechen.ireader.App
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.model.bean.BillBookBean
import com.example.newbiechen.ireader.ui.base.adapter.ViewHolderImpl
import com.example.newbiechen.ireader.utils.Constant

class BillBookHolder : ViewHolderImpl<BillBookBean>() {
    private lateinit var mIvPortrait: ImageView
    private lateinit var mTvTitle: TextView
    private lateinit var mTvAuthor: TextView
    private lateinit var mTvBrief: TextView
    private lateinit var mTvMsg: TextView
    override fun getItemLayoutId() = R.layout.item_book_brief

    override fun initView() {
        mIvPortrait = findById(R.id.book_brief_iv_portrait)
        mTvTitle = findById(R.id.book_brief_tv_title)
        mTvAuthor = findById(R.id.book_brief_tv_author)
        mTvBrief = findById(R.id.book_brief_tv_brief)
        mTvMsg = findById(R.id.book_brief_tv_msg)
    }

    override fun onBind(value: BillBookBean, pos: Int) {
        //头像
        Glide.with(App.getInstance())
                .load(Constant.IMG_BASE_URL + value.cover)
                .placeholder(R.drawable.ic_default_portrait)
                .error(R.drawable.ic_load_error)
                .fitCenter()
                .into(mIvPortrait)
        //书单名
        mTvTitle.text = value.title
        //作者
        mTvAuthor.text = value.author
        //简介
        mTvBrief.text = value.shortIntro
        //信息
        mTvMsg.text = App.getInstance().getString(R.string.nb_book_message,
                value.latelyFollower, value.retentionRatio)
    }

}