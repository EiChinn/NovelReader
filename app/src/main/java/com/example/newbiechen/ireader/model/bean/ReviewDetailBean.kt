package com.example.newbiechen.ireader.model.bean

data class ReviewDetailBean(
        val _id: String,
        val rating: Int,
        val content: String,
        val title: String,
        val type: String,
        val book: ReviewBookBean,
        val author: AuthorBean,
        val helpful: BookHelpfulBean,
        val state: String,
        val updated: String,
        val created: String,
        val commentCount: Int
)