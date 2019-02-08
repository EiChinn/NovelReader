package com.example.newbiechen.ireader.model.bean

data class CommentDetailBean(
        val _id: String,
        val title: String,
        val content: String,
        val author: AuthorBean,
        val type: String,
        val likeCount: Int,
        val isStick: Boolean,
        val block: String,
        val state: String,
        val updated: String,
        val created: String,
        val commentCount: Int,
        val voteCount: Int
)