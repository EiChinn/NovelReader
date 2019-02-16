package com.example.newbiechen.ireader.ui.base.adapter

import android.view.View
import android.view.ViewGroup

interface IViewHolder<T> {
    fun createItemView(parent: ViewGroup): View

    fun initView()

    fun onBind(value: T, pos: Int)

    fun onClick()
}