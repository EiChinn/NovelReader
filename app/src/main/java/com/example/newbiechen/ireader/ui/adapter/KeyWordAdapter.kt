package com.example.newbiechen.ireader.ui.adapter

import com.example.newbiechen.ireader.ui.adapter.view.KeyWordHolder
import com.example.newbiechen.ireader.ui.base.adapter.BaseListAdapter
import com.example.newbiechen.ireader.ui.base.adapter.IViewHolder

/**
 * Created by newbiechen on 17-6-2.
 */

class KeyWordAdapter : BaseListAdapter<String>() {
    override fun createViewHolder(viewType: Int): IViewHolder<String> {
        return KeyWordHolder()
    }
}
