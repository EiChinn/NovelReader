package com.example.newbiechen.ireader.model.bean

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.example.newbiechen.ireader.ui.adapter.LeaderBoardAdapter
import com.google.gson.annotations.SerializedName

data class BillboardBean(
        val _id: String,
        val title: String,
        val cover: String,
        @SerializedName("collapse")
        val isCollapse: Boolean,
        val monthRank: String,
        val totalRank: String

) : MultiItemEntity {
    override fun getItemType() = LeaderBoardAdapter.TYPE_OTHER

    constructor(_id: String) : this(_id, "", "", false, "", "")
}