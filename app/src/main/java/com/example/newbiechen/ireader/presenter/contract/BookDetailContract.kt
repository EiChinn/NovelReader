package com.example.newbiechen.ireader.presenter.contract

import com.example.newbiechen.ireader.db.entity.CollBook
import com.example.newbiechen.ireader.model.bean.BookDetailBean
import com.example.newbiechen.ireader.model.bean.BookListBean
import com.example.newbiechen.ireader.model.bean.HotCommentBean
import com.example.newbiechen.ireader.ui.base.BaseContract

interface BookDetailContract {
    interface View : BaseContract.BaseView {
        fun finishRefresh(bean: BookDetailBean)
        fun finishHotComment(beans: List<HotCommentBean>)
        fun finishRecommendBookList(beans: List<BookListBean>)
        fun waitToBookShelf()
        fun errorToBookShelf()
        fun succeedToBookShelf()
    }

    interface Presenter : BaseContract.BasePresenter<View> {
        fun refreshBookDetail(bookId: String)
        fun addToBookShelf(collBook: CollBook)
    }
}