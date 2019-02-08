package com.example.newbiechen.ireader.model.flag

import androidx.annotation.StringRes
import com.example.newbiechen.ireader.App
import com.example.newbiechen.ireader.R

enum class BookListType(@StringRes val typeNameId: Int, val netName: String) {
    HOT(R.string.nb_fragment_book_list_hot, "last-seven-days"),
    NEWEST(R.string.nb_fragment_book_list_newest, "created"),
    COLLECT(R.string.nb_fragment_book_list_collect, "collectorCount")
    ;

    fun getTypeName(): String {
        return App.getInstance().resources.getString(typeNameId)
    }
}
