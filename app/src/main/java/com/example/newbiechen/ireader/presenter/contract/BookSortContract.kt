package com.example.newbiechen.ireader.presenter.contract

import com.example.newbiechen.ireader.model.bean.packages.BookSortPackage
import com.example.newbiechen.ireader.model.bean.packages.BookSubSortPackage
import com.example.newbiechen.ireader.ui.base.BaseContract

interface BookSortContract {
    interface View : BaseContract.BaseView {
        fun finishRefresh(sortPackage: BookSortPackage, subSortPackage: BookSubSortPackage)
    }

    interface Presenter : BaseContract.BasePresenter<View> {
        fun refreshSortBean()
    }
}