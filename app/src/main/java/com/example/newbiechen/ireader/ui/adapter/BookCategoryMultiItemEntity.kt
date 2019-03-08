package com.example.newbiechen.ireader.ui.adapter

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.example.newbiechen.ireader.model.bean.BookSortBean

class BookCategoryMultiItemEntity(private val itemType: Int, val category: BookSortBean, val gender: String = "") : MultiItemEntity {
    companion object {
        const val ITEM_LABEL = 0xff01
        const val ITEM_CATEGORY = 0xff02
    }
    override fun getItemType() = itemType

}