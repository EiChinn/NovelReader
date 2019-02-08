package com.example.newbiechen.ireader.model.bean.packages

import com.example.newbiechen.ireader.model.bean.HotCommentBean

data class HotCommentPackage(
        val ok: Boolean,
        val reviews: List<HotCommentBean>
)