package com.example.newbiechen.ireader.model.bean

data class TagBookBean(
        val _id: String,
        val title: String,
        val author: String,
        val shortIntro: String,
        val cover: String,
        val cat: String,
        val majorCate: String,
        val minorCate: String,
        val latelyFollower: Int,
        val retentionRatio: Double,
        val lastChapter: String,
        val tags: List<String>
)