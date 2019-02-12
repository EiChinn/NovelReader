package com.example.newbiechen.ireader.ui.base.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView

class BaseViewHolder<T>(itemView: View, @JvmField val holder: IViewHolder<T>) : RecyclerView.ViewHolder(itemView) {
    init {
        holder.initView()
    }

}