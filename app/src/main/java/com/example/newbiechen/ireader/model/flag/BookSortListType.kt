package com.example.newbiechen.ireader.model.flag

enum class BookSortListType(val typeName: String, val netName: String) {
    HOT("热门", "hot"),
    NEW("新书", "new"),
    REPUTATION("好评", "reputation"),
    OVER("完结", "over");
}