package com.example.newbiechen.ireader.presenter

import com.example.newbiechen.ireader.model.bean.BookReviewBean
import com.example.newbiechen.ireader.model.flag.BookDistillate
import com.example.newbiechen.ireader.model.flag.BookSort
import com.example.newbiechen.ireader.model.flag.BookType
import com.example.newbiechen.ireader.model.local.LocalRepository
import com.example.newbiechen.ireader.model.remote.RemoteRepository
import com.example.newbiechen.ireader.presenter.contract.DiscReviewContract
import com.example.newbiechen.ireader.ui.base.RxPresenter
import com.example.newbiechen.ireader.utils.LogUtils.e
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by newbiechen on 17-4-21.
 */

class DiscReviewPresenter : RxPresenter<DiscReviewContract.View>(), DiscReviewContract.Presenter {
    private var isLocalLoad = false

    override fun firstLoading(sort: BookSort, bookType: BookType,
                              start: Int, limited: Int, distillate: BookDistillate) {
        //获取数据库中的数据
        val localObserver = LocalRepository.getInstance()
                .getBookReviews(sort.dbName, bookType.getNetName(),
                        start, limited, distillate.dbName)
        val remoteObserver = RemoteRepository.instance
                .getBookReviews(sort.getNetName(), bookType.getNetName(),
                        start, limited, distillate.getNetName())

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

    override fun refreshBookReview(sort: BookSort, bookType: BookType,
                                   start: Int, limited: Int, distillate: BookDistillate) {
        val refreshDispo = RemoteRepository.instance
                .getBookReviews(sort.getNetName(), bookType.getNetName(),
                        start, limited, distillate.getNetName())
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

    override fun loadingBookReview(sort: BookSort, bookType: BookType,
                                   start: Int, limited: Int, distillate: BookDistillate) {
        if (isLocalLoad) {
            val single = LocalRepository.getInstance()
                    .getBookReviews(sort.dbName, bookType.getNetName(),
                            start, limited, distillate.dbName)
            loadBookReview(single)
        } else {
            //单纯的加载数据
            val single = RemoteRepository.instance
                    .getBookReviews(sort.getNetName(), bookType.getNetName(),
                            start, limited, distillate.getNetName())
            loadBookReview(single)
        }
    }

    override fun saveBookReview(beans: List<BookReviewBean>) {
        LocalRepository.getInstance()
                .saveBookReviews(beans)
    }

    private fun loadBookReview(observable: Single<List<BookReviewBean>>) {
        val loadDispo = observable.subscribeOn(Schedulers.io())
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
