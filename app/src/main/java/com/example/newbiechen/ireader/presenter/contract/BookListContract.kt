package com.example.newbiechen.ireader.presenter.contract

import com.example.newbiechen.ireader.model.bean.BookListBean
import com.example.newbiechen.ireader.model.flag.BookListType
import com.example.newbiechen.ireader.ui.base.BaseContract

interface BookListContract {
    interface View : BaseContract.BaseView {
        fun finishRefresh(beans: List<BookListBean>)
        fun finishLoading(beans: List<BookListBean>)
        fun showLoadError()
    }

    interface Presenter : BaseContract.BasePresenter<View> {
        fun refreshBookList(type: BookListType, tag: String, start: Int, limited: Int)
        fun loadBookList(type: BookListType, tag: String, start: Int, limited: Int)
    }
}