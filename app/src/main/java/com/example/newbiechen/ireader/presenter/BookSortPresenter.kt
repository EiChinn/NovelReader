package com.example.newbiechen.ireader.presenter

import com.example.newbiechen.ireader.model.bean.packages.BookSortPackage
import com.example.newbiechen.ireader.model.bean.packages.BookSubSortPackage
import com.example.newbiechen.ireader.model.remote.RemoteRepository
import com.example.newbiechen.ireader.presenter.contract.BookSortContract
import com.example.newbiechen.ireader.ui.base.RxPresenter
import com.example.newbiechen.ireader.utils.LogUtils
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers

/**
 * Created by newbiechen on 17-4-23.
 */

class BookSortPresenter : RxPresenter<BookSortContract.View>(), BookSortContract.Presenter {
    override fun refreshSortBean() {
        //这个最好是设定一个默认时间采用Remote加载，如果Remote加载失败则采用数据中的数据。我这里先写死吧
        val sortSingle = RemoteRepository.getInstance()
                .bookSortPackage
        val subSortSingle = RemoteRepository.getInstance()
                .bookSubSortPackage

        val zipSingle = Single.zip(sortSingle, subSortSingle,
                BiFunction<BookSortPackage, BookSubSortPackage, SortPackage> { bookSortPackage, subSortPackage -> SortPackage(bookSortPackage, subSortPackage) })

        val disposable = zipSingle.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { bean ->
                            mView.finishRefresh(bean.sortPackage, bean.subSortPackage)
                            mView.complete()
                        },
                        { e ->
                            mView.showError()
                            LogUtils.e(e.toString())
                        }
                )
        addDisposable(disposable)

    }

    internal inner class SortPackage(var sortPackage: BookSortPackage, var subSortPackage: BookSubSortPackage)
}
