package com.example.newbiechen.ireader.ui.adapter

import android.view.View
import android.view.ViewGroup

import com.example.newbiechen.ireader.ui.adapter.view.CategoryHolder
import com.example.newbiechen.ireader.ui.base.EasyAdapter
import com.example.newbiechen.ireader.ui.base.adapter.IViewHolder
import com.example.newbiechen.ireader.widget.page.TxtChapter

/**
 * Created by newbiechen on 17-6-5.
 */

class CategoryAdapter : EasyAdapter<TxtChapter>() {
    private var currentSelected = 0
    override fun onCreateViewHolder(viewType: Int): IViewHolder<TxtChapter> {
        return CategoryHolder()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = super.getView(position, convertView, parent)
        val holder = view.tag as CategoryHolder

        if (position == currentSelected) {
            holder.setSelectedChapter()
        }

        return view
    }

    fun setChapter(pos: Int) {
        currentSelected = pos
        notifyDataSetChanged()
    }
}
