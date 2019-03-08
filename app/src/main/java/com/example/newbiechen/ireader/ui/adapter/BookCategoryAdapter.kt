package com.example.newbiechen.ireader.ui.adapter

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.example.newbiechen.ireader.R

class BookCategoryAdapter(data: List<BookCategoryMultiItemEntity>) : BaseMultiItemQuickAdapter<BookCategoryMultiItemEntity, BaseViewHolder>(data) {

    init {
        addItemType(BookCategoryMultiItemEntity.ITEM_LABEL, R.layout.item_book_category_label)
        addItemType(BookCategoryMultiItemEntity.ITEM_CATEGORY, R.layout.item_book_category_category)
    }
    override fun convert(helper: BaseViewHolder, item: BookCategoryMultiItemEntity) {
        when (item.itemType) {
            BookCategoryMultiItemEntity.ITEM_LABEL -> {
                helper.setText(R.id.tv_label, item.category.name)
            }
            BookCategoryMultiItemEntity.ITEM_CATEGORY -> {
                helper.setText(R.id.tv_category, item.category.name)
                helper.setText(R.id.tv_book_count, item.category.bookCount.toString())
            }
        }

    }

}