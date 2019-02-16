package com.example.newbiechen.ireader.ui.adapter.view

import android.view.View
import android.widget.ImageView
import android.widget.TextView

import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.model.bean.BookSourcesBean
import com.example.newbiechen.ireader.ui.base.adapter.ViewHolderImpl

import java.text.SimpleDateFormat

class ChangeSourceHolder(private val currentSource: String) : ViewHolderImpl<BookSourcesBean>() {

    private var mIvPortrait: ImageView? = null
    private var mTvSource: TextView? = null
    private var mTvUpdateInfo: TextView? = null
    private var mIvCurrentChose: ImageView? = null

    override fun getItemLayoutId(): Int {
        return R.layout.item_book_source
    }

    override fun initView() {
        mIvPortrait = findById(R.id.change_source_iv_portrait)
        mTvSource = findById(R.id.change_source_tv_book_source)
        mTvUpdateInfo = findById(R.id.change_source_tv_update_info)
        mIvCurrentChose = findById(R.id.change_source_iv_current_chose)
    }

    override fun onBind(value: BookSourcesBean, pos: Int) {

        //头像
        //		Glide.with(App.Companion.getInstance())
        //				.load(Constant.IMG_BASE_URL + value.getCover())
        //				.placeholder(R.drawable.ic_default_portrait)
        //				.error(R.drawable.ic_load_error)
        //				.fitCenter()
        //				.into(mIvPortrait);
        // 源网站
        val source = value.name
        mTvSource!!.text = source
        //作者
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        mTvUpdateInfo!!.text = String.format("%s: %s", dateFormat.format(value.updated), value.lastChapter)
        //简介
        if (source == currentSource) {
            mIvCurrentChose!!.visibility = View.VISIBLE
        } else {
            mIvCurrentChose!!.visibility = View.GONE
        }
    }
}
