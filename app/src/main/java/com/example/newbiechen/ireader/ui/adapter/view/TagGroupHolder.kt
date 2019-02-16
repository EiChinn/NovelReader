package com.example.newbiechen.ireader.ui.adapter.view

import android.widget.TextView

import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.ui.base.adapter.ViewHolderImpl

/**
 * Created by newbiechen on 17-5-5.
 */

class TagGroupHolder : ViewHolderImpl<String>() {
    private var mTvGroupName: TextView? = null

    override fun initView() {
        mTvGroupName = findById(R.id.tag_group_name)
    }

    override fun onBind(value: String, pos: Int) {
        mTvGroupName!!.text = value
    }

    override fun getItemLayoutId(): Int {
        return R.layout.item_tag_group
    }
}
