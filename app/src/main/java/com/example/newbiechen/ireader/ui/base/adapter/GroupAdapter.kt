package com.example.newbiechen.ireader.ui.base.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class GroupAdapter<T, R>(recyclerView: RecyclerView, spanSize: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val TYPE_GROUP = 1
        private const val TYPE_CHILD = 2
    }

    interface OnGroupClickListener {
        fun onGroupClick(view: View, pos: Int)
    }
    interface OnChildClickListener {
        fun onChildClick(view: View, groupPos: Int, childPos: Int)
    }

    fun setOnChildItemListener(listener: OnChildClickListener) {
        mChildClickListener = listener
    }

    private var mGroupListener: OnGroupClickListener? = null
    private var mChildClickListener: OnChildClickListener? = null

    abstract fun getGroupCount(): Int
    abstract fun getChildCount(groupPos: Int): Int

    abstract fun getGroupItem(groupPos: Int): T
    abstract fun getChildItem(groupPos: Int, childPos: Int): R

    protected abstract fun createGroupViewHolder(): IViewHolder<T>
    protected abstract fun createChildViewHolder(): IViewHolder<R>

    init {
        val manager = GridLayoutManager(recyclerView.context, spanSize)
        manager.spanSizeLookup = GroupSpanSizeLookup(spanSize)
        recyclerView.layoutManager = manager
    }

    inner class GroupSpanSizeLookup(private val maxSize: Int) : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            return if (getItemViewType(position) == TYPE_GROUP) maxSize else 1
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val iViewHolder = if (viewType == TYPE_GROUP) createGroupViewHolder() else createChildViewHolder()
        val view = iViewHolder.createItemView(parent)
        return BaseViewHolder(view, iViewHolder)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val type = getItemViewType(position)
        if (type == TYPE_GROUP) {
            val iHolder = (holder as BaseViewHolder<T>).holder
            val groupPos = calculateGroup(position)
            holder.itemView.setOnClickListener {
                iHolder.onClick()
                mGroupListener?.onGroupClick(it, groupPos)
            }
            iHolder.onBind(getGroupItem(groupPos), groupPos)
        } else {
            val groupPos = calculateGroup(position)
            val childPos = calculateChild(position)
            val iHolder = (holder as BaseViewHolder<R>).holder
            holder.itemView.setOnClickListener {
                iHolder.onClick()
                mChildClickListener?.onChildClick(it, groupPos, childPos)
            }
            iHolder.onBind(getChildItem(groupPos, childPos), childPos)

        }
    }

    /**
     * //计算position是哪个group中的头
     */
    private fun calculateGroup(position: Int): Int {
        var position = position
        for (i in 0 until getGroupCount()) {
            val total = getChildCount(i) + 1
            val loc = position - total
            if (loc < 0) {
                return position - 1
            } else {
                position = loc
            }
        }
        return -1
    }
    /**
     * //计算position是哪个group中的头
     */
    private fun calculateChild(position: Int): Int {
        var total = 0
        for (i in 0 until getGroupCount()) {
            total += getChildCount(i) + 1
            if (total > position) {
                return i
            }
        }
        return -1
    }

    override fun getItemCount(): Int {
        val groupCount = getGroupCount()
        var totalCount = groupCount
        for (i in 0 until groupCount) {
            totalCount += getChildCount(i)
        }

        return totalCount

    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return TYPE_GROUP
        }
        var position = position
        for (i in 0 until getGroupCount()) {
            val total = getChildCount(i) + 1
            if (position == 0) {
                return TYPE_GROUP
            } else if (position < 0) {
                return TYPE_CHILD
            }
            position -= total

        }
        //剩下的肯定是最后一行
        return TYPE_CHILD
    }


}