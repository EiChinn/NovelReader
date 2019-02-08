package com.example.newbiechen.ireader.presenter.contract

import com.example.newbiechen.ireader.model.bean.CollBookBean
import com.example.newbiechen.ireader.ui.base.BaseContract

interface BookShelfContract {
    interface View : BaseContract.BaseView {
        fun finishRefresh(collBookBeans: List<@JvmSuppressWildcards CollBookBean>)
        fun finishUpdate()
        fun showErrorTip(error: String)
    }

    interface Presenter : BaseContract.BasePresenter<View> {
        fun refreshCollBooks()
        fun createDownloadTask(CollBookBean: CollBookBean)
        fun updateCollBooks(collBookBeans: List<@JvmSuppressWildcards CollBookBean>)
        fun loadRecommendBooks(gender: String)
    }
}