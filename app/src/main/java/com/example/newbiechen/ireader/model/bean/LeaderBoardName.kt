package com.example.newbiechen.ireader.model.bean

import com.chad.library.adapter.base.entity.AbstractExpandableItem
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.example.newbiechen.ireader.ui.adapter.LeaderBoardAdapter

data class LeaderBoardName(
        val _id: String,
        val title: String,
        val cover: String
) : AbstractExpandableItem<BillboardBean>(), MultiItemEntity {
    override fun getItemType() = LeaderBoardAdapter.TYPE_NAME

    override fun getLevel() = 0

}