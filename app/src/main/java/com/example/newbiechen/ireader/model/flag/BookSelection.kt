package com.example.newbiechen.ireader.model.flag

enum class BookSelection(val converts: Array<out BookConvert>) {
    DISTILLATE(BookDistillate.values()),
    SORT_TYPE(BookSort.values()),
    BOOK_TYPE(BookType.values());

    fun getTypeParams(): List<String> {
        val params = mutableListOf<String>()
        converts.forEach {
            params.add(it.getTypeName())
        }
        return params
    }
}