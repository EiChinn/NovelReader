package com.example.newbiechen.ireader.model.bean

import com.google.gson.annotations.SerializedName

data class BillboardBean(
        val _id: String,
        val title: String,
        val cover: String,
        @SerializedName("collapse")
        val isCollapse: Boolean,
        val monthRank: String,
        val totalRank: String

) {
    constructor(_id: String) : this(_id, "", "", false, "", "")
}