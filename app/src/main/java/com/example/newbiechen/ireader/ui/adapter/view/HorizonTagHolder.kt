package com.example.newbiechen.ireader.ui.adapter.view

import android.widget.TextView

import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.ui.base.adapter.ViewHolderImpl

/**
 * Created by newbiechen on 17-5-2.
 */

class HorizonTagHolder : ViewHolderImpl<String>() {

    private var mTvName: TextView? = null

    override fun initView() {
        mTvName = findById(R.id.horizon_tag_tv_name)
    }

    override fun onBind(value: String, pos: Int) {
        mTvName!!.text = value
        mTvName!!.setTextColor(getContext().resources.getColor(R.color.nb_text_common_h2))
    }

    fun setSelectedTag() {
        mTvName!!.setTextColor(getContext().resources.getColor(R.color.light_red))
    }

    override fun getItemLayoutId(): Int {
        return R.layout.item_horizon_tag
    }
}
