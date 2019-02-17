package com.example.newbiechen.ireader.presenter

import com.example.newbiechen.ireader.model.remote.RemoteRepository
import com.example.newbiechen.ireader.presenter.contract.CommentDetailContract
import com.example.newbiechen.ireader.ui.base.RxPresenter
import com.example.newbiechen.ireader.utils.LogUtils
import com.example.newbiechen.ireader.utils.RxUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by newbiechen on 17-4-29.
 */

class CommentDetailPresenter : RxPresenter<CommentDetailContract.View>(), CommentDetailContract.Presenter {

    override fun refreshCommentDetail(detailId: String, start: Int, limit: Int) {
        val detailSingle = RemoteRepository
                .instance.getCommentDetail(detailId)

        val bestCommentsSingle = RemoteRepository
                .instance.getBestComments(detailId)

        val commentsSingle = RemoteRepository
                .instance.getDetailComments(detailId, start, limit)

        val detailDispo = RxUtils.toCommentDetail(detailSingle, bestCommentsSingle, commentsSingle)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { (detail, bestComments, comments) ->
                            mView.finishRefresh(detail,
                                    bestComments, comments)
                            mView.complete()
                        },
                        { e ->
                            mView.showError()
                            LogUtils.e(e.toString())
                        }
                )
        addDisposable(detailDispo)
    }

    override fun loadComment(detailId: String, start: Int, limit: Int) {
        val loadDispo = RemoteRepository.instance
                .getDetailComments(detailId, start, limit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { bean -> mView.finishLoad(bean) },
                        { e ->
                            mView.showLoadError()
                            LogUtils.e(e.toString())
                        }
                )
        addDisposable(loadDispo)
    }
}
