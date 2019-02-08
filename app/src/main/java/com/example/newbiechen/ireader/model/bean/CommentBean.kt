package com.example.newbiechen.ireader.model.bean

data class CommentBean(
        val _id: String,
        val content: String,
        val author: AuthorBean,
        val floor: Int,
        val likeCount: Int,
        val created: String,
        val replyTo: ReplyToBean
)