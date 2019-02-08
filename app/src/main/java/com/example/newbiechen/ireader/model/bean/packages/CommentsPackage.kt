package com.example.newbiechen.ireader.model.bean.packages

import com.example.newbiechen.ireader.model.bean.CommentBean

data class CommentsPackage(
        val ok: Boolean,
        val comments: List<CommentBean>
)