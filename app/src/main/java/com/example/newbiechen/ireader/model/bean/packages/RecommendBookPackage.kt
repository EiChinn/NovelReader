package com.example.newbiechen.ireader.model.bean.packages

import com.example.newbiechen.ireader.model.bean.CollBookBean

data class RecommendBookPackage(
        val ok: Boolean,
        val books: List<CollBookBean>
)