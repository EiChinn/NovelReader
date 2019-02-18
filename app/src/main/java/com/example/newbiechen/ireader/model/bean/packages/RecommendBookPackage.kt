package com.example.newbiechen.ireader.model.bean.packages

import com.example.newbiechen.ireader.db.entity.CollBook

data class RecommendBookPackage(
        val ok: Boolean,
        val books: List<CollBook>
)