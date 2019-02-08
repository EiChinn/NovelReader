package com.example.newbiechen.ireader.model.bean.packages

import com.example.newbiechen.ireader.model.bean.BookSortBean

data class BookSortPackage(val ok: Boolean, val male: List<BookSortBean>, val female: List<BookSortBean>)