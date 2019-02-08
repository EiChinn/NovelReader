package com.example.newbiechen.ireader.model.bean.packages

import com.example.newbiechen.ireader.model.bean.BookListBean

data class RecommendBookListPackage(
        val ok: Boolean,
        val booklists: List<BookListBean>
)