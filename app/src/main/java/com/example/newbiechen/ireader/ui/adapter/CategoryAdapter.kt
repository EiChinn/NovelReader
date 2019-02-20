package com.example.newbiechen.ireader.ui.adapter

import android.widget.TextView
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.example.newbiechen.ireader.App
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.utils.BookManager
import com.example.newbiechen.ireader.widget.page.TxtChapter

class CategoryAdapter(data: List<TxtChapter>) : BaseQuickAdapter<TxtChapter, BaseViewHolder>(R.layout.item_category, data) {
    override fun convert(helper: BaseViewHolder, item: TxtChapter) {
        //TODO:目录显示设计的有点不好，需要靠成员变量是否为null来判断。
        //首先判断是否该章已下载
        //如果没有链接地址表示是本地文件
        val drawable = if (item.link.isEmpty()) {
            ContextCompat.getDrawable(App.getInstance(), R.drawable.selector_category_load)
        } else {
            if (item.bookId.isNotBlank() && BookManager.isChapterCached(item.bookId, item.title)) {
                ContextCompat.getDrawable(App.getInstance(), R.drawable.selector_category_load)
            } else {
                ContextCompat.getDrawable(App.getInstance(), R.drawable.selector_category_unload)
            }
        }

        val category_tv_chapter = helper.getView<TextView>(R.id.category_tv_chapter)
        if (item.isSelected) {
            category_tv_chapter.setTextColor(ContextCompat.getColor(App.getInstance(), R.color.light_red))
        } else {
            category_tv_chapter.setTextColor(ContextCompat.getColor(App.getInstance(), R.color.nb_text_default))
        }
        category_tv_chapter.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        category_tv_chapter.text = item.title
    }

}