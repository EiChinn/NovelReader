package com.example.newbiechen.ireader.ui.base

import android.app.Service
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseService : Service() {
    private lateinit var mDisposable: CompositeDisposable

    protected fun addDisposable(d: Disposable) {
        if (!this::mDisposable.isInitialized) {
            mDisposable = CompositeDisposable()
        }
        mDisposable.add(d)
    }

    override fun onDestroy() {
        if (this::mDisposable.isInitialized) {
            mDisposable.clear()
        }
        super.onDestroy()
    }
}