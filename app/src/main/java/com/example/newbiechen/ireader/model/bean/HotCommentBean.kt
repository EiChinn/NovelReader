package com.example.newbiechen.ireader.model.bean

data class HotCommentBean(
        val _id: String,
        val rating: Int,
        val content: String,
        val title: String,
        val author: AuthorBean,
        val helpful: BookHelpfulBean,
        val likeCount: Int,
        val state: String,
        val updated: String,
        val created: String,
        val commentCount: Int
)