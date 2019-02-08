package com.example.newbiechen.ireader.model.bean

data class BillBookBean(
        val _id: String,
        val title: String,
        val author: String,
        val shortIntro: String,
        val cover: String,
        val cat: String,
        val site: String,
        val banned: Int,
        val latelyFollower: Int,
        val retentionRatio: String

)