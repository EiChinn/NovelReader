package com.example.newbiechen.ireader.ui.base

abstract class BaseMVPActivity<K : BaseContract.BaseView, T : BaseContract.BasePresenter<K>> : BaseActivity(), BaseContract.BaseView {
    protected lateinit var  mPresenter: T

    protected abstract fun bindPresenter(): T

    override fun processLogic() {
        attachView(bindPresenter())
    }

    private fun attachView(presenter: T) {
        mPresenter = presenter
        mPresenter.attachView(this as K)
    }

    override fun onDestroy() {
        if (this::mPresenter.isInitialized) {
            mPresenter.detachView()
        }
        super.onDestroy()
    }
}