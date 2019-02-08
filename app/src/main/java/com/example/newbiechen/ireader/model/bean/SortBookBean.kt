package com.example.newbiechen.ireader.model.bean

data class SortBookBean(
        val _id: String,
        val title: String,
        val author: String,
        val shortIntro: String,
        val cover: String,
        val site: String,
        val majorCate: String,
        val banned: Int,
        val latelyFollower: Int,
        val retentionRatio: Double,
        val lastChapter: String,
        val tags: List<String>
)