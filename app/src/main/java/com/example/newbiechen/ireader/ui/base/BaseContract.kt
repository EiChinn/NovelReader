package com.example.newbiechen.ireader.ui.base

interface BaseContract {
    interface BasePresenter<T : BaseView> {
        fun attachView(view: T)

        fun detachView()
    }

    interface BaseView {
        fun showError()

        fun complete()
    }
}