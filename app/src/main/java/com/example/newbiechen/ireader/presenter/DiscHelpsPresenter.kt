package com.example.newbiechen.ireader.presenter

import com.example.newbiechen.ireader.model.bean.BookHelpsBean
import com.example.newbiechen.ireader.model.flag.BookDistillate
import com.example.newbiechen.ireader.model.flag.BookSort
import com.example.newbiechen.ireader.model.local.LocalRepository
import com.example.newbiechen.ireader.model.remote.RemoteRepository
import com.example.newbiechen.ireader.presenter.contract.DiscHelpsContract
import com.example.newbiechen.ireader.ui.base.RxPresenter
import com.example.newbiechen.ireader.utils.LogUtils.e
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by newbiechen on 17-4-21.
 */

class DiscHelpsPresenter : RxPresenter<DiscHelpsContract.View>(), DiscHelpsContract.Presenter {
    private var isLocalLoad = false

    override fun firstLoading(sort: BookSort, start: Int, limited: Int, distillate: BookDistillate) {
        //获取数据库中的数据
        val localObserver = LocalRepository.getInstance()
                .getBookHelps(sort.dbName, start, limited, distillate.dbName)
        val remoteObserver = RemoteRepository.instance
                .getBookHelps(sort.getNetName(), start, limited, distillate.getNetName())

        Single.concat(localObserver, remoteObserver)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { beans -> mView.finishRefresh(beans) },
                        { e ->
                            isLocalLoad = true
                            mView.complete()
                            mView.showErrorTip()
                            e(e.toString())
                        },
                        {
                            isLocalLoad = false
                            mView.complete()
                        }
                )
    }

    override fun refreshBookHelps(sort: BookSort, start: Int, limited: Int, distillate: BookDistillate) {
        val refreshDispo = RemoteRepository.instance
                .getBookHelps(sort.getNetName(), start, limited, distillate.getNetName())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { beans ->
                            isLocalLoad = false
                            mView.finishRefresh(beans)
                            mView.complete()
                        },
                        { e ->
                            mView.complete()
                            mView.showErrorTip()
                            e(e.toString())
                        }
                )
        addDisposable(refreshDispo)
    }

    override fun loadingBookHelps(sort: BookSort, start: Int, limited: Int, distillate: BookDistillate) {
        if (isLocalLoad) {
            val single = LocalRepository.getInstance()
                    .getBookHelps(sort.dbName, start, limited, distillate.dbName)
            loadBookHelps(single)
        } else {
            val single = RemoteRepository.instance
                    .getBookHelps(sort.getNetName(), start, limited, distillate.getNetName())
            loadBookHelps(single)
        }
    }

    override fun saveBookHelps(beans: List<BookHelpsBean>) {
        LocalRepository.getInstance()
                .saveBookHelps(beans)
    }

    private fun loadBookHelps(observable: Single<List<BookHelpsBean>>) {
        val loadDispo = observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { beans -> mView.finishLoading(beans) },
                        { e ->
                            mView.showError()
                            e(e.toString())
                        }
                )
        addDisposable(loadDispo)
    }
}
