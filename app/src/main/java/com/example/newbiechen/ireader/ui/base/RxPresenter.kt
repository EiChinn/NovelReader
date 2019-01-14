package com.example.newbiechen.ireader.ui.base

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class RxPresenter<T : BaseContract.BaseView> : BaseContract.BasePresenter<T> {
    protected lateinit var mView: T
    protected lateinit var mDisposable: CompositeDisposable


    protected fun addDisposable(subscription: Disposable) {
        if (!this::mDisposable.isInitialized) {
            mDisposable = CompositeDisposable()
        }
        mDisposable.add(subscription)
    }

    protected fun unSubscribe() {
        if (this::mDisposable.isInitialized && !mDisposable.isDisposed) {
            mDisposable.dispose()
        }
    }
    override fun attachView(view: T) {
        mView = view

    }

    override fun detachView() {
//        mView = null
        unSubscribe()
    }

}