package com.example.newbiechen.ireader.ui.adapter.view

import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.ui.base.adapter.ViewHolderImpl

/**
 * Created by newbiechen on 17-5-2.
 */

class TagChildHolder : ViewHolderImpl<String>() {
    private var mTvName: TextView? = null
    private var mSelectTag = -1

    override fun initView() {
        mTvName = findById(R.id.tag_child_btn_name)
    }

    override fun onBind(value: String, pos: Int) {
        mTvName!!.text = value
        //这里要重置点击事件
        if (mSelectTag == pos) {
            mTvName!!.setTextColor(ContextCompat.getColor(getContext(), R.color.light_red))
        } else {
            mTvName!!.setTextColor(ContextCompat.getColor(getContext(), R.color.nb_text_default))
        }
    }

    fun setTagSelected(pos: Int) {
        mSelectTag = pos
    }

    override fun getItemLayoutId(): Int {
        return R.layout.item_tag_child
    }
}