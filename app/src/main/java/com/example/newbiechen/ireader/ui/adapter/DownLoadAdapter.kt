package com.example.newbiechen.ireader.ui.adapter

import com.example.newbiechen.ireader.model.bean.DownloadTaskBean
import com.example.newbiechen.ireader.ui.adapter.view.DownloadHolder
import com.example.newbiechen.ireader.ui.base.adapter.BaseListAdapter
import com.example.newbiechen.ireader.ui.base.adapter.IViewHolder

/**
 * Created by newbiechen on 17-5-12.
 */

class DownLoadAdapter : BaseListAdapter<DownloadTaskBean>() {

    override fun createViewHolder(viewType: Int): IViewHolder<DownloadTaskBean> {
        return DownloadHolder()
    }
}
