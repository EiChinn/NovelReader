package com.example.newbiechen.ireader.model.bean

data class DetailBean<T>(
        val detail: T,
        val bestComments: List<CommentBean>,
        val comments: List<CommentBean>
)