package com.example.newbiechen.ireader.model.flag

enum class BookSortListType(val typeName: String, val netName: String) {
    HOT("热门", "hot"),
    NEW("新书", "new"),
    REPUTATION("好评", "reputation"),
    OVER("完结", "over");

    companion object {
        fun getNetName(typeName: String): String {
            values().forEach {
                if (it.typeName == typeName) {
                    return it.netName
                }
            }
            return HOT.netName
        }
    }

}