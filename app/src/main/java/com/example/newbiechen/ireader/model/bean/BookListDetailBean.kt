package com.example.newbiechen.ireader.model.bean

data class BookListDetailBean (
        val _id: String,
        val updated: String,
        val title: String,
        val author: AuthorBean,
        val desc: String,
        val gender: String,
        val created: String,
        val stickStopTime: Any,
        val isDraft: Boolean,
        val isDistillate: Any,
        val collectorCount: Int,
        val shareLink: String,
        val id: String,
        val total: Int,
        val tags: List<String>,
        val books: List<DetailBooksBean>
)

data class DetailBooksBean(
        val book: DetailBookBean,
        val comment: String
)

data class DetailBookBean(
        val cat: String,
        val _id: String,
        val title: String,
        val author: String,
        val longIntro: String,
        val majorCate: String,
        val minorCate: String,
        val cover: String,
        val site: String,
        val banned: Int,
        val latelyFollower: Int,
        val wordCount: Int,
        val retentionRatio: Double
)