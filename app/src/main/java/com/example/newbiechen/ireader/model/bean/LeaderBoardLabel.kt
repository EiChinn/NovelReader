package com.example.newbiechen.ireader.model.bean

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.example.newbiechen.ireader.ui.adapter.LeaderBoardAdapter

data class LeaderBoardLabel(
        val label: String
) : MultiItemEntity {
    override fun getItemType()= LeaderBoardAdapter.TYPE_LABEL

}