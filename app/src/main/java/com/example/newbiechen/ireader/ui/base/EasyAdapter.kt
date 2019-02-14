package com.example.newbiechen.ireader.ui.base

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.newbiechen.ireader.ui.base.adapter.IViewHolder

abstract class EasyAdapter<T> : BaseAdapter() {
    private val mList = mutableListOf<T>()

    override fun getCount(): Int {
        return mList.size
    }

    override fun getItem(position: Int): T {
        return mList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun addItems(values: List<T>) {
        mList.addAll(values)
        notifyDataSetChanged()
    }
    fun refreshItems(values: List<T>) {
        mList.clear()
        mList.addAll(values)
        notifyDataSetChanged()
    }

    fun getItems(): List<T> {
        return mList
    }

    fun clear() {
        mList.clear()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val holder: IViewHolder<T>?
        val result: View
        if (convertView == null) {
            holder = onCreateViewHolder(getItemViewType(position))
            result = holder.createItemView(parent!!)
            result.tag = holder
            //初始化
            holder.initView()
        } else {
            result = convertView
            holder = convertView.tag as IViewHolder<T>
        }
        //执行绑定
        holder.onBind(getItem(position), position)
        return result
    }

    protected abstract fun onCreateViewHolder(viewType: Int): IViewHolder<T>
}