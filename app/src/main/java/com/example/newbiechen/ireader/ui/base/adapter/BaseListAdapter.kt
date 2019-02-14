package com.example.newbiechen.ireader.ui.base.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.util.*

abstract class BaseListAdapter<T> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mList = mutableListOf<T>()
    private var mClickListener: OnItemClickListener? = null
    private var mLongClickListener: OnItemLongClickListener? = null

    /************************abstract area************************/
    protected abstract fun createViewHolder(viewType: Int): IViewHolder<T>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder = createViewHolder(viewType)
        val view = viewHolder.createItemView(parent)
        //初始化
        return BaseViewHolder(view, viewHolder)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val iHolder = (holder as BaseViewHolder<T>).holder
        iHolder.onBind(getItem(position), position)

        //设置点击事件
        holder.itemView.setOnClickListener {
            mClickListener?.onItemClick(it, position)

            //adapter监听点击事件
            iHolder.onClick()
            onItemClick(it, position)
        }

        //设置长点击事件
        holder.itemView.setOnLongClickListener {
            val isClicked = mLongClickListener?.onItemLongClick(it, position) ?: false
            onItemLongClick(it, position)
            return@setOnLongClickListener isClicked

        }

    }

    override fun getItemCount() = mList.size

    protected open fun onItemClick(v: View, pos: Int) { }
    protected fun onItemLongClick(v: View, pos: Int) { }

    /******************************public area***********************************/
    fun setOnItemClickListener(listener: OnItemClickListener?) {
        mClickListener = listener
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener?) {
        mLongClickListener = listener
    }
    open fun addItem(value: T) {
        mList.add(value)
        notifyDataSetChanged()
    }

    open fun addItem(index: Int, value: T) {
        mList.add(index, value)
        notifyDataSetChanged()
    }

    open fun addItems(values: List<@JvmSuppressWildcards T>) {
        mList.addAll(values)
        notifyDataSetChanged()
    }

    open fun removeItem(value: T) {
        mList.remove(value)
        notifyDataSetChanged()
    }

    open fun removeItems(values: List<@JvmSuppressWildcards T>) {
        mList.removeAll(values)
        notifyDataSetChanged()
    }

    fun getItem(position: Int): T = mList[position]

    fun getItems(): List<T> = Collections.unmodifiableList(mList)

    fun getItemSize() = mList.size

    open fun refreshItems(list: List<@JvmSuppressWildcards T>) {
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

    fun clear() {
        mList.clear()
    }

    /***************************inner class area***********************************/
    interface OnItemClickListener {
        fun onItemClick(view: View, pos: Int)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(view: View, pos: Int): Boolean
    }
}