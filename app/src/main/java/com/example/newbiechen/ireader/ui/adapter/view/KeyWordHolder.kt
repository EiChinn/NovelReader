package com.example.newbiechen.ireader.ui.adapter.view

import android.widget.TextView

import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.ui.base.adapter.ViewHolderImpl

/**
 * Created by newbiechen on 17-6-2.
 */

class KeyWordHolder : ViewHolderImpl<String>() {

    private var mTvName: TextView? = null

    override fun initView() {
        mTvName = findById(R.id.keyword_tv_name)
    }

    override fun onBind(data: String, pos: Int) {
        mTvName!!.text = data
    }

    override fun getItemLayoutId(): Int {
        return R.layout.item_keyword
    }
}
