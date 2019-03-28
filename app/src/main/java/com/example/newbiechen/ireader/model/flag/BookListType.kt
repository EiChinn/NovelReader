package com.example.newbiechen.ireader.model.flag

enum class BookListType(val typeName: String, val netName: String) {
    HOT("本周最热", "last-seven-days"),
    NEWEST("最新发布", "created"),
    COLLECT("最多收藏", "collectorCount")
    ;

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
