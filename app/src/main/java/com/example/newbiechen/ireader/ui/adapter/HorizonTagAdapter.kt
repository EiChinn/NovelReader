package com.example.newbiechen.ireader.ui.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.newbiechen.ireader.ui.adapter.view.HorizonTagHolder
import com.example.newbiechen.ireader.ui.base.adapter.BaseListAdapter
import com.example.newbiechen.ireader.ui.base.adapter.BaseViewHolder
import com.example.newbiechen.ireader.ui.base.adapter.IViewHolder

/**
 * Created by newbiechen on 17-5-2.
 */

class HorizonTagAdapter : BaseListAdapter<String>() {
    private var currentSelected = 0

    override fun createViewHolder(viewType: Int): IViewHolder<String> {
        return HorizonTagHolder()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        //配置点击事件改变状态
        val iHolder = (holder as BaseViewHolder<*>).holder
        val horizonTagHolder = iHolder as HorizonTagHolder
        if (position == currentSelected) {
            horizonTagHolder.setSelectedTag()
        }
    }

    /***
     * 设定当前的点击事件
     * @param pos
     */
    fun setCurrentSelected(pos: Int) {
        selectTag(pos)
    }

    override fun onItemClick(v: View, pos: Int) {
        super.onItemClick(v, pos)
        selectTag(pos)
    }

    private fun selectTag(position: Int) {
        currentSelected = position
        notifyDataSetChanged()
    }
}
