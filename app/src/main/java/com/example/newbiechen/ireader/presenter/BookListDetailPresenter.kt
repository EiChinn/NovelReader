package com.example.newbiechen.ireader.presenter

import com.example.newbiechen.ireader.model.remote.RemoteRepository
import com.example.newbiechen.ireader.presenter.contract.BookListDetailContract
import com.example.newbiechen.ireader.ui.base.RxPresenter
import com.example.newbiechen.ireader.utils.LogUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by newbiechen on 17-5-2.
 */

class BookListDetailPresenter : RxPresenter<BookListDetailContract.View>(), BookListDetailContract.Presenter {
    override fun refreshBookListDetail(detailId: String) {
        val refreshDispo = RemoteRepository.instance
                .getBookListDetail(detailId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { beans ->
                            mView.finishRefresh(beans)
                            mView.complete()
                        },
                        { e ->
                            mView.showError()
                            LogUtils.e(e.toString())
                        }
                )
        addDisposable(refreshDispo)
    }
}
