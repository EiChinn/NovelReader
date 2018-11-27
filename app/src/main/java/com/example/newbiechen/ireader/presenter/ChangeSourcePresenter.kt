package com.example.newbiechen.ireader.presenter

import com.example.newbiechen.ireader.model.bean.BookSourcesBean
import com.example.newbiechen.ireader.model.remote.RemoteRepository
import com.example.newbiechen.ireader.presenter.contract.ChangeSourceContract
import com.example.newbiechen.ireader.ui.base.RxPresenter
import com.example.newbiechen.ireader.utils.LogUtils
import com.example.newbiechen.ireader.utils.RxUtils

class ChangeSourcePresenter : RxPresenter<ChangeSourceContract.View>(), ChangeSourceContract.Presenter {
    override fun loadSources(bookId: String) {
        val disposable = RemoteRepository.getInstance()
                .getBookSources(bookId)
                .compose<List<BookSourcesBean>> { RxUtils.toSimpleSingle(it) }
                .subscribe(
                        { beans -> mView.showSource(beans) },
                        { e ->
                            //TODO: Haven't grate conversation method.
                            LogUtils.e(e)
                        }
                )
        addDisposable(disposable)
    }

}