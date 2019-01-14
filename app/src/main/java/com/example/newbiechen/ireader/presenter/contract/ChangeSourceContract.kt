package com.example.newbiechen.ireader.presenter.contract

import com.example.newbiechen.ireader.model.bean.BookSourcesBean
import com.example.newbiechen.ireader.ui.base.BaseContract

interface ChangeSourceContract{
    interface View : BaseContract.BaseView {
        fun showSource(bookChapterList: List<BookSourcesBean>)
    }

    interface Presenter : BaseContract.BasePresenter<View> {
        fun loadSources(bookId: String)
    }
}