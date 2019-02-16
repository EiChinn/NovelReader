package com.example.newbiechen.ireader.ui.adapter.view

import android.widget.ImageView
import android.widget.TextView

import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.model.bean.SectionBean
import com.example.newbiechen.ireader.ui.base.adapter.ViewHolderImpl

/**
 * Created by newbiechen on 17-4-16.
 */

class SectionHolder : ViewHolderImpl<SectionBean>() {

    private var mIvIcon: ImageView? = null
    private var mTvName: TextView? = null

    override fun initView() {
        mIvIcon = findById(R.id.section_iv_icon)
        mTvName = findById(R.id.section_tv_name)
    }

    override fun onBind(value: SectionBean, pos: Int) {
        mIvIcon!!.setImageResource(value.drawableId)
        mTvName!!.text = value.name
    }

    override fun getItemLayoutId(): Int {
        return R.layout.item_section
    }
}
