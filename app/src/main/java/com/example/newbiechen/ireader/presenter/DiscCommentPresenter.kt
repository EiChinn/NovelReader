package com.example.newbiechen.ireader.presenter

import com.example.newbiechen.ireader.model.bean.BookCommentBean
import com.example.newbiechen.ireader.model.flag.BookDistillate
import com.example.newbiechen.ireader.model.flag.BookSort
import com.example.newbiechen.ireader.model.flag.CommunityType
import com.example.newbiechen.ireader.model.local.LocalRepository
import com.example.newbiechen.ireader.model.remote.RemoteRepository
import com.example.newbiechen.ireader.presenter.contract.DiscCommentContract
import com.example.newbiechen.ireader.ui.base.RxPresenter
import com.example.newbiechen.ireader.utils.LogUtils.e
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by newbiechen on 17-4-20.
 */

class DiscCommentPresenter : RxPresenter<DiscCommentContract.View>(), DiscCommentContract.Presenter {
    //是否采取直接从数据库加载
    private var isLocalLoad = true

    override fun firstLoading(block: CommunityType, sort: BookSort, start: Int, limited: Int, distillate: BookDistillate) {
        //获取数据库中的数据
        val localObserver = LocalRepository.getInstance()
                .getBookComments(block.netName, sort.dbName,
                        start, limited, distillate.dbName)
        val remoteObserver = RemoteRepository.instance
                .getBookComment(block.netName, sort.getNetName(),
                        start, limited, distillate.getNetName())

        //这里有问题，但是作者却用的好好的，可能是2.0之后的问题
        val disposable = localObserver
                .concatWith(remoteObserver)
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
        addDisposable(disposable)
    }

    override fun refreshComment(block: CommunityType, sort: BookSort,
                                start: Int, limited: Int, distillate: BookDistillate) {
        val refreshDispo = RemoteRepository.instance
                .getBookComment(block.netName, sort.getNetName(),
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

    override fun loadingComment(block: CommunityType, sort: BookSort, start: Int, limited: Int, distillate: BookDistillate) {
        if (isLocalLoad) {
            val single = LocalRepository.getInstance()
                    .getBookComments(block.netName, sort.dbName,
                            start, limited, distillate.dbName)
            loadComment(single)
        } else {
            //单纯的加载数据
            val single = RemoteRepository.instance
                    .getBookComment(block.netName, sort.getNetName(),
                            start, limited, distillate.getNetName())
            loadComment(single)

        }
    }

    private fun loadComment(observable: Single<List<BookCommentBean>>) {
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

    override fun saveComment(beans: List<BookCommentBean>) {
        LocalRepository.getInstance()
                .saveBookComments(beans)
    }
}
