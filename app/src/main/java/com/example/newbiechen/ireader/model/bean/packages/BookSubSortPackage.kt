package com.example.newbiechen.ireader.model.bean.packages

import com.example.newbiechen.ireader.model.bean.BookSubSortBean

data class BookSubSortPackage(
        val ok: Boolean,
        val male: List<BookSubSortBean>,
        val female: List<BookSubSortBean>,
        val press: List<BookSubSortBean>
)