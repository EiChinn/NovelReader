package com.example.newbiechen.ireader.presenter.contract

import com.example.newbiechen.ireader.model.bean.BookListDetailBean
import com.example.newbiechen.ireader.ui.base.BaseContract

interface BookListDetailContract {
    interface View : BaseContract.BaseView {
        fun finishRefresh(bean: BookListDetailBean)
    }

    interface Presenter : BaseContract.BasePresenter<View> {
        fun refreshBookListDetail(detailId: String)
    }
}