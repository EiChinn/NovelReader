package com.example.newbiechen.ireader.presenter

import com.example.newbiechen.ireader.model.flag.BookSortListType
import com.example.newbiechen.ireader.model.remote.RemoteRepository
import com.example.newbiechen.ireader.presenter.contract.BookSortListContract
import com.example.newbiechen.ireader.ui.base.RxPresenter
import com.example.newbiechen.ireader.utils.LogUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by newbiechen on 17-5-3.
 */

class BookSortListPresenter : RxPresenter<BookSortListContract.View>(), BookSortListContract.Presenter {
    override fun refreshSortBook(gender: String, type: BookSortListType, major: String, minor: String, start: Int, limit: Int) {
        var minor = minor

        if (minor == "全部") {
            minor = ""
        }

        val refreshDispo = RemoteRepository.instance
                .getSortBooks(gender, type.netName, major, minor, start, limit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { beans ->
                            mView.finishRefresh(beans)
                            mView.complete()
                        },
                        { e ->
                            mView.complete()
                            mView.showError()
                            LogUtils.e(e.toString())
                        }
                )
        addDisposable(refreshDispo)
    }

    override fun loadSortBook(gender: String, type: BookSortListType, major: String, minor: String, start: Int, limit: Int) {
        val loadDispo = RemoteRepository.instance
                .getSortBooks(gender, type.netName, major, minor, start, limit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { beans -> mView.finishLoad(beans) },
                        { e ->
                            mView.showLoadError()
                            LogUtils.e(e.toString())
                        }
                )
        addDisposable(loadDispo)
    }
}
