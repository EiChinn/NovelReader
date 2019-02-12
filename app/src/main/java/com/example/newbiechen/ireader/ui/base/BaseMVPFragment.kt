package com.example.newbiechen.ireader.ui.base

abstract class BaseMVPFragment<K : BaseContract.BaseView, T : BaseContract.BasePresenter<K>> : BaseFragment(), BaseContract.BaseView{
    protected lateinit var mPresenter: T

    protected abstract fun bindPresenter(): T

    override fun processLogic() {
        super.processLogic()
        mPresenter = bindPresenter()
        mPresenter.attachView(this as K)

    }

    override fun onDestroy() {
        mPresenter.detachView()
        super.onDestroy()
    }
}