package com.example.newbiechen.ireader.ui.adapter

import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.newbiechen.ireader.ui.adapter.view.PageStyleHolder
import com.example.newbiechen.ireader.ui.base.adapter.BaseListAdapter
import com.example.newbiechen.ireader.ui.base.adapter.BaseViewHolder
import com.example.newbiechen.ireader.ui.base.adapter.IViewHolder
import com.example.newbiechen.ireader.widget.page.PageStyle

/**
 * Created by newbiechen on 17-5-19.
 */

class PageStyleAdapter : BaseListAdapter<Drawable>() {
    private var currentChecked: Int = 0

    override fun createViewHolder(viewType: Int): IViewHolder<Drawable> {
        return PageStyleHolder()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val iHolder = (holder as BaseViewHolder<*>).holder
        val pageStyleHolder = iHolder as PageStyleHolder
        if (currentChecked == position) {
            pageStyleHolder.setChecked()
        }
    }

    fun setPageStyleChecked(pageStyle: PageStyle) {
        currentChecked = pageStyle.ordinal
    }

    override fun onItemClick(v: View, pos: Int) {
        super.onItemClick(v, pos)
        currentChecked = pos
        notifyDataSetChanged()
    }
}
