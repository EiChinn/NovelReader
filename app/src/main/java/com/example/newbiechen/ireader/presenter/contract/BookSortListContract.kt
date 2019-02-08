package com.example.newbiechen.ireader.presenter.contract

import com.example.newbiechen.ireader.model.bean.SortBookBean
import com.example.newbiechen.ireader.model.flag.BookSortListType
import com.example.newbiechen.ireader.ui.base.BaseContract

interface BookSortListContract {
    interface View : BaseContract.BaseView {
        fun finishRefresh(beans: List<SortBookBean>)
        fun finishLoad(beans: List<SortBookBean>)
        fun showLoadError()
    }

    interface Presenter : BaseContract.BasePresenter<View> {
        fun refreshSortBook(gender: String, type: BookSortListType, major: String, minor: String, start: Int, limit: Int)
        fun loadSortBook(gender: String, type: BookSortListType, major: String, minor: String, start: Int, limit: Int)
    }
}