package com.example.newbiechen.ireader.presenter.contract

import com.example.newbiechen.ireader.model.bean.BookHelpsBean
import com.example.newbiechen.ireader.model.flag.BookDistillate
import com.example.newbiechen.ireader.model.flag.BookSort
import com.example.newbiechen.ireader.ui.base.BaseContract

interface DiscHelpsContract{
    interface View : BaseContract.BaseView {
        fun finishRefresh(beans: List<@JvmSuppressWildcards BookHelpsBean>)
        fun finishLoading(beans: List<@JvmSuppressWildcards BookHelpsBean>)
        fun showErrorTip()
    }

    interface Presenter : BaseContract.BasePresenter<View> {
        fun firstLoading(sort: BookSort, start: Int, limited: Int, distillate: BookDistillate)
        fun refreshBookHelps(sort: BookSort, start: Int, limited: Int, distillate: BookDistillate)
        fun loadingBookHelps(sort: BookSort, start: Int, limited: Int, distillate: BookDistillate)
        fun saveBookHelps(beans: List<@JvmSuppressWildcards BookHelpsBean>)
    }
}