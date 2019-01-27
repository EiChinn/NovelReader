package com.example.newbiechen.ireader.ui.base.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class ViewHolderImpl<T> : IViewHolder<T> {
    private var view: View? = null
    private var context: Context? = null

    protected abstract fun getItemLayoutId(): Int

    override fun createItemView(parent: ViewGroup): View {
        context = parent.context
        view = LayoutInflater.from(parent.context).inflate(getItemLayoutId(), parent, false)

        return view!!

    }

    protected fun <V : View> findById(id: Int): V {
        return view!!.findViewById(id)
    }

    protected fun getContext() = context!!

    override fun onClick() {

    }
}