package com.example.newbiechen.ireader.presenter.contract

import com.example.newbiechen.ireader.model.bean.BookReviewBean
import com.example.newbiechen.ireader.model.flag.BookDistillate
import com.example.newbiechen.ireader.model.flag.BookSort
import com.example.newbiechen.ireader.model.flag.BookType
import com.example.newbiechen.ireader.ui.base.BaseContract

interface DiscReviewContract{
    interface View : BaseContract.BaseView {
        fun finishRefresh(beans: List<@JvmSuppressWildcards BookReviewBean>)
        fun finishLoading(beans: List<@JvmSuppressWildcards BookReviewBean>)
        fun showErrorTip()
    }

    interface Presenter : BaseContract.BasePresenter<View> {
        fun firstLoading(sort: BookSort, bookType: BookType, start: Int, limited: Int, distillate: BookDistillate)
        fun refreshBookReview(sort: BookSort, bookType: BookType, start: Int, limited: Int, distillate: BookDistillate)
        fun loadingBookReview(sort: BookSort, bookType: BookType, start: Int, limited: Int, distillate: BookDistillate)
        fun saveBookReview(beans: List<@JvmSuppressWildcards BookReviewBean>)
    }
}