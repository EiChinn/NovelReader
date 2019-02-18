package com.example.newbiechen.ireader.presenter.contract

import com.example.newbiechen.ireader.db.entity.CollBook
import com.example.newbiechen.ireader.ui.base.BaseContract

interface BookShelfContract {
    interface View : BaseContract.BaseView {
        fun finishRefresh(collBooks: List<CollBook>)
        fun finishUpdate()
        fun showErrorTip(error: String)
    }

    interface Presenter : BaseContract.BasePresenter<View> {
        fun refreshCollBooks()
        fun createDownloadTask(CollBookBean: CollBook)
        fun updateCollBooks(collBookBeans: List<CollBook>)
    }
}