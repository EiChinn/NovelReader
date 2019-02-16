package com.example.newbiechen.ireader.ui.adapter.view

import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.model.bean.DetailBookBean
import com.example.newbiechen.ireader.ui.base.adapter.ViewHolderImpl
import com.example.newbiechen.ireader.utils.Constant

class BookListInfoHolder : ViewHolderImpl<DetailBookBean>() {
    private lateinit var mIvPortrait: ImageView
    private lateinit var mTvTitle: TextView
    private lateinit var mTvAuthor: TextView
    private lateinit var mTvMsg: TextView
    private lateinit var mTvWord: TextView
    private lateinit var mTvContent: TextView
    override fun getItemLayoutId() = R.layout.item_book_list_info

    override fun initView() {
        mIvPortrait = findById(R.id.book_list_info_iv_cover)
        mTvTitle = findById(R.id.book_list_info_tv_title)
        mTvAuthor = findById(R.id.book_list_info_tv_author)
        mTvContent = findById(R.id.book_list_info_tv_content)
        mTvMsg = findById(R.id.book_list_info_tv_msg)
        mTvWord = findById(R.id.book_list_info_tv_word)
    }

    override fun onBind(value: DetailBookBean, pos: Int) {
        //头像
        Glide.with(getContext())
                .load(Constant.IMG_BASE_URL + value.cover)
                .placeholder(R.drawable.ic_book_loading)
                .error(R.drawable.ic_load_error)
                .fitCenter()
                .into(mIvPortrait)
        //书单名
        mTvTitle.text = value.title
        //作者
        mTvAuthor.text = value.author
        //简介
        mTvContent.text = value.longIntro
        //信息
        mTvMsg.text = getContext().resources.getString(R.string.nb_book_message,
                value.latelyFollower, value.retentionRatio)
        //书籍字数
        mTvWord.text = getContext().resources.getString(R.string.nb_book_word, value.wordCount / 10000)
    }

}