package com.example.newbiechen.ireader.ui.adapter.view

import android.graphics.drawable.Drawable
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.ui.base.adapter.ViewHolderImpl
import com.example.newbiechen.ireader.utils.BookManager
import com.example.newbiechen.ireader.widget.page.TxtChapter

/**
 * Created by newbiechen on 17-5-16.
 */

class CategoryHolder : ViewHolderImpl<TxtChapter>() {

    private var mTvChapter: TextView? = null

    override fun initView() {
        mTvChapter = findById(R.id.category_tv_chapter)
    }

    override fun onBind(value: TxtChapter, pos: Int) {
        //首先判断是否该章已下载
        var drawable: Drawable? = null

        //TODO:目录显示设计的有点不好，需要靠成员变量是否为null来判断。
        //如果没有链接地址表示是本地文件
        if (value.link == null) {
            drawable = ContextCompat.getDrawable(getContext(), R.drawable.selector_category_load)
        } else {
            if (value.bookId != null && BookManager
                            .isChapterCached(value.bookId, value.title)) {
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.selector_category_load)
            } else {
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.selector_category_unload)
            }
        }

        mTvChapter!!.isSelected = false
        mTvChapter!!.setTextColor(ContextCompat.getColor(getContext(), R.color.nb_text_default))
        mTvChapter!!.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        mTvChapter!!.text = value.title
    }

    override fun getItemLayoutId(): Int {
        return R.layout.item_category
    }

    fun setSelectedChapter() {
        mTvChapter!!.setTextColor(ContextCompat.getColor(getContext(), R.color.light_red))
        mTvChapter!!.isSelected = true
    }
}
