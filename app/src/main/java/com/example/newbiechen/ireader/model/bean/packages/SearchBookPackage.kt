package com.example.newbiechen.ireader.model.bean.packages

data class SearchBookPackage(
        val ok: Boolean,
        val books: List<SearchBooksBean>
)

data class SearchBooksBean(
        val _id: String,
        val hasCp: Boolean,
        val title: String,
        val cat: String,
        val author: String,
        val site: String,
        val cover: String,
        val shortIntro: String,
        val lastChapter: String,
        val retentionRatio: Double,
        val banned: Int,
        val latelyFollower: Int,
        val wordCount: Int
)