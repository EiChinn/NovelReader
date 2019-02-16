package com.example.newbiechen.ireader.ui.adapter.view

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView

import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.ui.base.adapter.ViewHolderImpl

/**
 * Created by newbiechen on 17-5-19.
 */

class PageStyleHolder : ViewHolderImpl<Drawable>() {

    private var mReadBg: View? = null
    private var mIvChecked: ImageView? = null

    override fun initView() {
        mReadBg = findById(R.id.read_bg_view)
        mIvChecked = findById(R.id.read_bg_iv_checked)
    }

    override fun onBind(data: Drawable, pos: Int) {
        mReadBg!!.background = data
        mIvChecked!!.visibility = View.GONE
    }

    override fun getItemLayoutId(): Int {
        return R.layout.item_read_bg
    }

    fun setChecked() {
        mIvChecked!!.visibility = View.VISIBLE
    }
}
